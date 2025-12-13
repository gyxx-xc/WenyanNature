package indi.wenyan.interpreter.exec_interface;

import net.minecraft.world.phys.Vec3;

/**
 * Interface representing a device that can execute Wenyan command sent by Wenyan platforms
 * has position to show connect effect
 */
public interface IWenyanPositionedDevice extends IWenyanDevice {
    /**
     * For connecting effect only
     *
     * @return The position of this device in the world
     */
    Vec3 getPosition();
}
