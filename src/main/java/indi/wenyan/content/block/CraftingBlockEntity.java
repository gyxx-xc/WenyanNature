package indi.wenyan.content.block;

import indi.wenyan.content.checker.EchoChecker;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.interpreter.structure.WenyanProgram;
import indi.wenyan.interpreter.utils.CraftingAnswerChecker;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingBlockEntity extends BlockEntity implements MenuProvider {
    private boolean isCrafting = false;
    private BlockRunner runner;
    private CraftingAnswerChecker checker;

    // for gui
    public CraftingAnswerChecker.Result result;
    public int round = 0;
    public final int maxRound = 80;
    protected final ContainerData data;

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_ENTITY.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> CraftingBlockEntity.this.round;
                    case 1 -> CraftingBlockEntity.this.maxRound;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int v) {
                // do nothing
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    // consider it as a State Machine
    // with isCrafting and isRunning as the state
    // 00. idle
    // 10. crafting
    // 11. running
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, CraftingBlockEntity entity) {
        if (!level.isClientSide()) {
            if (entity.isCrafting && !entity.runner.program.isRunning()) {
                entity.round ++;
                entity.result = entity.checker.getResult();
                if (entity.result != CraftingAnswerChecker.Result.ANSWER_CORRECT) {
                    entity.isCrafting = false;
                } else if (entity.round >= entity.maxRound) {
                    entity.isCrafting = false;
                    entity.ejectItem();
                } else {
                    entity.checker = new EchoChecker(level.getRandom());
                    entity.runner.program = new WenyanProgram(String.join("\n", entity.runner.pages),
                            WenyanPackages.craftingEnvironment(entity.checker), null);
                    entity.runner.program.run();
                }
            }
        }
    }

    public void run(BlockRunner runner, Player player) {
        assert level != null;
        if (isCrafting) {
            return;
        }
        // TODO: change checker according to the recipe
        checker = new EchoChecker(level.getRandom());
        round = 0;

        this.runner = runner;
        runner.program = new WenyanProgram(String.join("\n", runner.pages),
                WenyanPackages.craftingEnvironment(checker), player);
        runner.program.run();
        isCrafting = true;
    }

    public void ejectItem() {
        BlockPos pos = worldPosition.relative(Direction.UP);
        assert level != null;
//        Block.popResource(level, pos, runner.extractItem(RUNNER_SLOT, 1, false));
        Block.popResource(level, pos, new ItemStack(Items.PAPER, 1));
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
        CompoundTag tag = super.getUpdateTag(registries);
        return tag;
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
}
