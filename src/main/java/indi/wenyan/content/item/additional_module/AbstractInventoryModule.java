package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.utils.ExecQueue;
import indi.wenyan.interpreter.utils.IWenyanBlockDevice;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractInventoryModule extends Item implements IWenyanBlockDevice {
    public AbstractInventoryModule(Properties properties) {
        super(properties);
    }

    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
