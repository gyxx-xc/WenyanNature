package indi.wenyan.content.block.pedestal;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PedestalBlockEntity extends BlockEntity implements Container {
    @Getter
    private final ResourceHandler<ItemResource> itemHandler = createItemHandler();

    public PedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.PEDESTAL_ENTITY.get(), pos, blockState);
    }

    private ResourceHandler<ItemResource> createItemHandler() {
        return new ItemStackResourceHandler() {
            @Override
            protected ItemStack getStack() {
                return null;
            }

            @Override
            protected void setStack(ItemStack stack) {

            }
        };
//                new ItemStackHandler(1) {
//            @Override
//            protected void onContentsChanged(int slot) {
//                setChanged();
//                assert level != null;
//                if (!level.isClientSide)
//                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
//            }
//
//            @Override
//            protected int getStackLimit(int slot, ItemStack stack) {
//                return 1;
//            }
//        };
    }

    // from com.hollingsworth.arsnouveau.common.block.tile.SingleItemTile
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return itemHandler.getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return itemHandler.getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        updateBlock();
        return itemHandler.extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return itemHandler.extractItem(pSlot, itemHandler.getSlotLimit(pSlot), false);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        itemHandler.setStackInSlot(pSlot, pStack);
        updateBlock();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return itemHandler.getStackInSlot(0).isEmpty();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void clearContent() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        updateBlock();
    }

    public void updateBlock() {
        if(level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    }

    public ItemStack getStack() {
        return itemHandler.getStackInSlot(0);
    }

    public void setStack(ItemStack stack) {
        itemHandler.setStackInSlot(0, stack);
        updateBlock();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
