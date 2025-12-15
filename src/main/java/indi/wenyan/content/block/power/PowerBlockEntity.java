package indi.wenyan.content.block.power;

import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicInteger;

public class PowerBlockEntity extends BlockEntity implements IWenyanDevice {
    public final AtomicInteger weakPower = new AtomicInteger(0);
    public final AtomicInteger strongPower = new AtomicInteger(0);

    public PowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            weakPower.decrementAndGet();
            strongPower.decrementAndGet();
        }
    }

    @Override
    public HandlerPackageBuilder.RawHandlerPackage getExecPackage() {
        return null;
    }

    @Override
    public String getPackageName() {
        return "";
    }
}
