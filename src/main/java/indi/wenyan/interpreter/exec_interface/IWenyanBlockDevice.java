package indi.wenyan.interpreter.exec_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface representing a device that can execute Wenyan command sent by Wenyan platforms
 * has position to show connect effect
 */
public interface IWenyanBlockDevice extends IWenyanDevice {
    /**
     * For connecting effect only
     *
     * @return The position of this device in the world
     */
    BlockState blockState();

    BlockPos blockPos();
}
