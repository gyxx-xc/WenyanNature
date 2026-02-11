package indi.wenyan.content.block.crafting_block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.content.recipe.AnsweringRecipeInput;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.CraftClearParticlePacket;
import indi.wenyan.setup.network.CraftingParticlePacket;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

// two logic of running it
// 1. accept the function for the answer
// > impl: may need to change the compile environment to
//   get a generated java code of a function
// > need to disable it if it is interacting
// 2. provide the corresponding vars and provide print function to check the answer
// > impl: logic for changed question within the round of running problem
// > provide function of 1. global var(n) 2. print(ans)

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingBlockEntity extends AbstractModuleEntity implements MenuProvider {
    private int craftingProgress;
    private IAnsweringChecker checker = null;
    private RecipeHolder<AnsweringRecipe> recipeHolder = null;

    // for gui deprecated
    public IAnsweringChecker.ResultStatus result;
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> craftingProgress;
                case 1 -> result != null ? result.ordinal() : -1;
                default -> 0;
            };
        }

        @Override
        public void set(int i, int v) {
            // do nothing
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private static final int RANGE = 3; // the offset to search for pedestals

    @Getter
    private final Deque<TextParticle> particles = new ArrayDeque<>();

    @Override
    public String getBasePackageName() {
        return "";
    }

    public static final String PARTICLE_ID = "particle";

    @Getter
    public final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「参」", request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException.WenyanVarException("「参」function takes no arguments.");
                return getChecker().getArgs();
            })
            .handler("书", request -> {
                getChecker().accept(request.args());
                IAnsweringChecker.ResultStatus checkerResult = checker.getResult();
                assert getLevel() != null;
                if (level instanceof ServerLevel sl) {
                    for (var arg : request.args()) {
                        PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                                new CraftingParticlePacket(getBlockPos(), arg.as(WenyanString.TYPE).toString()));
                    }
                }
                switch (checkerResult) {
                    case RUNNING -> {
                        // do nothing, remain keep running
                    }
                    case WRONG_ANSWER -> {
                        craftingProgress = 0;
                        if (level instanceof ServerLevel sl)
                            PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                                    new CraftClearParticlePacket(getBlockPos()));
                        checker.init();
                    }
                    case ANSWER_CORRECT -> {
                        craftingProgress ++;
                        checker.init();
                        if (craftingProgress >= recipeHolder.value().round()) {
                            // prevent sudden change of recipe, although not needed since one tick
                            getChecker();
                            craftingProgress = 0;
                            craftAndEjectItem();
                        }
                    }
                }
                return WenyanNull.NULL;
            })
            // TODO: .const of a builtin function
            .build();

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        for (var particle : particles) {
            if (particle.remainTick() > 0) {
                particle.remainTick --;
                particle.setPos(particle.pos.multiply(0.95, 0.95, 0.95));
            } else {
                // should be faster, since the particle remove from head
                particles.removeFirstOccurrence(particle);
            }
        }
    }

    // logic: if cached, if check recipe consistence -> return cached
    // else: recreate the checker
    private IAnsweringChecker getChecker() throws WenyanThrowException {
        Level level = getLevel();
        assert level != null;
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        forNearbyPedestal(level, blockPos(), pedestal -> pedestalItems.add(pedestal.getItem(0)));
        var optionalRecipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                new AnsweringRecipeInput(pedestalItems), level, this.recipeHolder); // set last recipe as hint
        if (optionalRecipeHolder.isEmpty()) {
            resetCrafting();
            throw new WenyanException("No valid recipe found for the current pedestal items.");
        }

        if (this.recipeHolder != null && this.recipeHolder.equals(optionalRecipeHolder.get())) {
            return checker;
        } else resetCrafting();
        this.recipeHolder = optionalRecipeHolder.get();
        var question = optionalRecipeHolder.get().value().question();
        var recipeChecker = CheckerFactory.produce(question, level.getRandom());
        checker = recipeChecker;
        checker.init(); // recreated, reset the checker state
        return recipeChecker;
    }

    public void craftAndEjectItem() {
        assert level != null;
        // TODO: for recipe with remaining item
        forNearbyPedestal(level, blockPos(), pedestal -> pedestal.setItem(0, ItemStack.EMPTY));
        BlockPos pos = worldPosition.relative(Direction.UP);
        Block.popResource(level, pos, recipeHolder.value().output().copy());
    }

    private void resetCrafting() {
        this.craftingProgress = 0;
        this.checker = null;
        this.recipeHolder = null;
        this.result = null;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CraftingBlockContainer(i, this, data);
    }

    public static void forNearbyPedestal(Level level, BlockPos pos, Consumer<PedestalBlockEntity> consumer) {
        for (BlockPos b : BlockPos.betweenClosed(pos.offset(RANGE, -RANGE, RANGE), pos.offset(-RANGE, RANGE, -RANGE))) {
            if (level.getBlockEntity(b) instanceof PedestalBlockEntity pedestal && !pedestal.getItem(0).isEmpty()) {
                consumer.accept(pedestal);
            }
        }
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveData(tag, registries);
        var effect = particles.stream().map(TextParticle::data).reduce((r, p) -> r + p);
        effect.ifPresent(s -> tag.putString(PARTICLE_ID, s));
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadData(tag, registries);
        if (tag.contains(PARTICLE_ID)) {
            assert level != null;
            particles.addAll(TextParticle.randomSplash(tag.getString(PARTICLE_ID), level.random));
        }
    }

    public void addResultParticle(String data) {
        assert level != null;
        particles.addAll(TextParticle.randomSplash(data, level.random));
    }

    public void clearParticles() {
        particles.clear();
    }

    @Data
    @Accessors(fluent = true)
    public static class TextParticle {
        final float rot;
        final String data;
        Vec3 oPos;
        Vec3 pos;
        int remainTick = 20;

        public TextParticle(Vec3 pos, float rot, String data) {
            this.oPos = pos;
            this.pos = pos;
            this.rot = rot;
            this.data = data;
        }

        public Vec3 getPosition(float partialTicks) {
            return oPos.lerp(pos, partialTicks);
        }

        public void setPos(Vec3 pos) {
            this.oPos = this.pos;
            this.pos = pos;
        }

        public static List<TextParticle> randomSplash(String data, RandomSource random) {
            final int range = 1;
            List<TextParticle> particles = new ArrayList<>();
            for (var c : data.toCharArray()) {
                Vec3 pos = new Vec3(random.triangle(0, range), random.triangle(0, range), random.triangle(0, range));
                particles.add(new TextParticle(pos, random.nextFloat() * 360, String.valueOf(c)));
            }
            return particles;
        }
    }
}
