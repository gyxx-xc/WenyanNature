package indi.wenyan.content.block;

import indi.wenyan.WenyanNature;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.content.recipe.AnsweringRecipeInput;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.function.Consumer;

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingBlockEntity extends BlockEntity implements MenuProvider {
    private boolean isCrafting = false;
    private BlockRunner runner;
    private CraftingAnswerChecker checker;
    private RecipeHolder<AnsweringRecipe> recipeHolder;
    private Player player; // the player who is crafting

    // for gui
    public CraftingAnswerChecker.Result result;
    public int round = 0;
    public final int maxRound = 16;
    protected final ContainerData data;
    private static final int RANGE = 3; // the offset to search for pedestals

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_ENTITY.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> CraftingBlockEntity.this.round;
                    case 1 -> CraftingBlockEntity.this.maxRound;
                    case 2 -> CraftingBlockEntity.this.isCrafting ? 1 : 0;
                    case 3 -> CraftingBlockEntity.this.result != null ? CraftingBlockEntity.this.result.ordinal() : -1;
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
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, CraftingBlockEntity entity) {
        if (!level.isClientSide()) {
            if (entity.isCrafting && !entity.runner.program.isRunning()) {
                entity.round ++;
                entity.result = entity.checker.getResult();

                // Check if the crafting is still valid
                ArrayList<ItemStack> pedestalItems = new ArrayList<>();
                forNearbyPedestal(level, pos, p -> pedestalItems.add(p.getItem(0)));
                var recipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                        new AnsweringRecipeInput(pedestalItems), level);
                if (recipeHolder.isEmpty() || !recipeHolder.get().equals(entity.recipeHolder)) {
                    entity.result = CraftingAnswerChecker.Result.RUNTIME_ERROR;
                }

                if (entity.result != CraftingAnswerChecker.Result.ANSWER_CORRECT) {
                    entity.isCrafting = false;
                } else if (entity.round >= entity.maxRound) {
                    entity.isCrafting = false;
                    forNearbyPedestal(level, pos, p -> p.removeItem(0, p.getMaxStackSize()));
                    entity.ejectItem();
                } else {
                    // TODO
                    entity.runner.program = new WenyanProgram(String.join("\n", entity.runner.pages),
                            WenyanPackageBuilder.create()
                                    .environment(WenyanPackages.CRAFTING_BASE_ENVIRONMENT)
                                    .environment(entity.checker.inputEnvironment()).build(),
                            entity.player, entity.checker);
                    entity.runner.program.run();
                }
            }
        }
    }

    public void run(BlockRunner runner, Player player) {
        assert level != null;
        if (isCrafting) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_nature.already_run").getString());
        }

        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        forNearbyPedestal(level, getBlockPos(), p -> pedestalItems.add(p.getItem(0)));
        var recipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                new AnsweringRecipeInput(pedestalItems), level);
        if (recipeHolder.isEmpty()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_nature.function_not_found_").getString());
            return;
        }

        this.recipeHolder = recipeHolder.get();
        var question = recipeHolder.get().value().question();
        checker = CheckerFactory.produce(question, level.getRandom());
        if (checker == null) {
            WenyanNature.LOGGER.error("Failed to create checker for question: {}", question);
            WenyanException.handleException(player, Component.translatable("error.wenyan_nature.function_not_found_").getString());
            return;
        }

        this.runner = runner;
        round = 0;
        this.player = player;
        runner.program = new WenyanProgram(String.join("\n", runner.pages),
                WenyanPackageBuilder.create()
                        .environment(WenyanPackages.CRAFTING_BASE_ENVIRONMENT)
                        .environment(checker.inputEnvironment()).build(), player, checker);
        runner.program.run();
        isCrafting = true;
    }

    public void ejectItem() {
        BlockPos pos = worldPosition.relative(Direction.UP);
        assert level != null;
        Block.popResource(level, pos, recipeHolder.value().output());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return super.getUpdateTag(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
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
}
