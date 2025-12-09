package indi.wenyan.content.block.crafting_block;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.content.recipe.AnsweringRecipeInput;
import indi.wenyan.interpreter.structure.WenyanException;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.function.Consumer;

// two logic of running it
// 1. accept the function for the answer
// > impl: may need builtin function with handwritten bytecode?
// 2. provide the corresponding vars and provide print function to check the answer
// > impl: logic for changed question within the round of running problem

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingBlockEntity extends BlockEntity implements MenuProvider {
    private boolean isCrafting;
    private RunnerBlockEntity runner;
    private CraftingAnswerChecker checker;
    private RecipeHolder<AnsweringRecipe> recipeHolder;

    // for gui
    public IAnsweringChecker.Result result;
    public int round;
    public final int maxRound = 16;
    protected final ContainerData data;
    private static final int RANGE = 3; // the offset to search for pedestals

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_BLOCK_ENTITY.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> round;
                    case 1 -> maxRound;
                    case 2 -> isCrafting ? 1 : 0;
                    case 3 -> result != null ? result.ordinal() : -1;
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
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            if (isCrafting && !runner.program.isRunning()) {
                round ++;
                result = checker.getResult();

                // Check if the crafting is still valid
                ArrayList<ItemStack> pedestalItems = new ArrayList<>();
                forNearbyPedestal(level, pos, p -> pedestalItems.add(p.getItem(0)));
                var recipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                        new AnsweringRecipeInput(pedestalItems), level);
                if (recipeHolder.isEmpty() || !recipeHolder.get().equals(this.recipeHolder)) {
                    result = IAnsweringChecker.Result.RUNTIME_ERROR;
                }

                // handle the result
                if (result != IAnsweringChecker.Result.ANSWER_CORRECT) {
                    isCrafting = false;
                } else if (round >= maxRound) {
                    isCrafting = false;
                    forNearbyPedestal(level, pos, p -> p.removeItem(0, p.getMaxStackSize()));
                    // TODO: not eject, may copy ComposterBlock
                    ejectItem();
                } else {
                    // continue
//                    runner.program = new WenyanProgram(runner.pages,
//                            WenyanPackages.CRAFTING_BASE_ENVIRONMENT,
//                            player, checker);
                    checker.init(runner.program);
                    runner.program.createThread();
                }
            }
        }
    }

    public void run(RunnerBlockEntity runner, Player player) {
        assert level != null;
        if (isCrafting) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }

        ArrayList<ItemStack> pedestalItems = new ArrayList<>();
        forNearbyPedestal(level, getBlockPos(), p -> pedestalItems.add(p.getItem(0)));
        var recipeHolder = level.getRecipeManager().getRecipeFor(Registration.ANSWERING_RECIPE_TYPE.get(),
                new AnsweringRecipeInput(pedestalItems), level);
        if (recipeHolder.isEmpty()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.function_not_found_").getString());
            return;
        }

        this.recipeHolder = recipeHolder.get();
        var question = recipeHolder.get().value().question();
        checker = CheckerFactory.produce(question, level.getRandom());
        if (checker == null) {
            WenyanProgramming.LOGGER.error("Failed to create checker for question: {}", question);
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.function_not_found_").getString());
            return;
        }

        this.runner = runner;
        round = 0;
//        runner.program = new WenyanProgram(runner.pages,
//                WenyanPackages.CRAFTING_BASE_ENVIRONMENT, player, checker);
        checker.init(runner.program);
        runner.program.createThread();
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
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
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
