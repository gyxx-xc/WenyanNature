package indi.wenyan.content.gui;

import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CraftingBlockContainer extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final ContainerData data;

    public CraftingBlockContainer(int windowId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(windowId, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(4));
    }

    public CraftingBlockContainer(int windowId, BlockEntity blockEntity, ContainerData data) {
        super(Registration.CRAFTING_CONTAINER.get(), windowId);
        this.blockEntity = blockEntity;
        this.data = data;
        addDataSlots(data);
    }

    public int getProgress(int length) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        if (this.data.get(2) == 0) // not crafting
            return length;
        return maxProgress == 0 ? 0 : progress * length / maxProgress;
    }

    public int getResult() {
        return this.data.get(3);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, Registration.CRAFTING_BLOCK.get());
    }
}
