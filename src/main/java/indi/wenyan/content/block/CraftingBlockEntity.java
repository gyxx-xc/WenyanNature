package indi.wenyan.content.block;

import indi.wenyan.content.checker.EchoChecker;
import indi.wenyan.interpreter.structure.WenyanProgram;
import indi.wenyan.interpreter.utils.CraftingAnswerChecker;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
public class CraftingBlockEntity extends BlockEntity {

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_ENTITY.get(), pos, blockState);
    }

    // consider it as a State Machine
    // with isCrafting and isRunning as the state
    // 00. idle
    // 01. error
    // 10. crafting
    // 11. running
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, CraftingBlockEntity entity) {

    }

    public void run(BlockRunner runner, Player player) {
        assert level != null;
        CraftingAnswerChecker checker = new EchoChecker(level.getRandom());
        runner.program = new WenyanProgram(String.join("\n", runner.pages),
                WenyanPackages.craftingEnvironment(checker), player);
        runner.program.run();
    }

    public void ejectItem() {
        BlockPos pos = worldPosition.relative(Direction.UP);
        assert level != null;
//        Block.popResource(level, pos, runner.extractItem(RUNNER_SLOT, 1, false));
    }

    public void setHolder() {
    }

    @Nonnull
    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
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
}
