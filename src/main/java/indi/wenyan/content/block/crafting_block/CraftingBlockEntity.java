package indi.wenyan.content.block.crafting_block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.content.recipe.AnsweringRecipeInput;
import indi.wenyan.interpreter.exec_interface.HandlerPackageBuilder;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
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

    // for gui
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
    public final String basePackageName = "builtin";

    @Getter
    public final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「参」", request -> {
                if (!request.args().isEmpty()) throw new WenyanException.WenyanVarException("「参」function takes no arguments.");
                return getChecker().getArgs();
            })
            .handler("书", request -> {
                getChecker().accept(request.args());
                switch (checker.getResult()) {
                    case RUNNING -> {}
                    case WRONG_ANSWER -> {
                        craftingProgress = 0;
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

    // logic: if cached, if check recipe consistence -> return cached
    // else: recreate the checker
    private IAnsweringChecker getChecker() {
        Level level = getLevel();
        assert level != null;
        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        forNearbyPedestal(level, blockPos(), pedestal -> pedestalItems.add(pedestal.getItem(0)));
        var recipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                new AnsweringRecipeInput(pedestalItems), level, this.recipeHolder); // set last recipe as hint
        if (recipeHolder.isEmpty()) {
            resetCrafting();
            throw new WenyanException("No valid recipe found for the current pedestal items.");
        }

        if (this.recipeHolder != null && this.recipeHolder.equals(recipeHolder.get())) {
            return checker;
        } else resetCrafting();
        this.recipeHolder = recipeHolder.get();
        var question = recipeHolder.get().value().question();
        var result = CheckerFactory.produce(question, level.getRandom());
        checker = result;
        checker.init(); // recreated, reset the checker state
        return result;
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
}
