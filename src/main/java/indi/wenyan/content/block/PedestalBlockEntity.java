package indi.wenyan.content.block;

import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class PedestalBlockEntity extends BlockEntity {
    private final ItemStackHandler item = createItemHandler();
    private boolean canInteract = true;

    public PedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.PEDESTAL_ENTITY.get(), pos, blockState);
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

            @Override
            protected int getStackLimit(int slot, @NotNull ItemStack stack) {
                return 1;
            }
        };
    }

    public ItemStack insertItem(ItemStack stack) {
        return item.insertItem(0, stack, false);
    }

    public ItemStack extractItem() {
        return item.extractItem(0, 1, false);
    }

    public ItemStack getItem() {
        return item.getStackInSlot(0);
    }

    public boolean canInteract() {
        return canInteract;
    }

    public void setInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }
}
