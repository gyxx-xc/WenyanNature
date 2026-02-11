package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.exec_interface.IWenyanDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface representing a device that can execute Wenyan command sent by Wenyan platforms
 * has position to show connect effect
 */
public interface IWenyanBlockDevice extends IWenyanDevice {
    BlockState blockState();

    BlockPos blockPos();

    boolean isRemoved();
}
