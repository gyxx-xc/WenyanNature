package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.exec_interface.IExecReceiver;
import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.handler.IContextExecCallHandler;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.ItemContext;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractInventoryModule extends Item implements IWenyanDevice {
    public AbstractInventoryModule(Properties properties) {
        super(properties);
    }

    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player)
            // FIXME: the request not this stack should not be run
            handle(new ItemContext(stack, level, player, slotId, isSelected));
    }

    abstract class ThisCallHandler implements IContextExecCallHandler {
        @Override
        public Optional<IExecReceiver> getExecutor() {
            return Optional.of(AbstractInventoryModule.this);
        }
    }
}
