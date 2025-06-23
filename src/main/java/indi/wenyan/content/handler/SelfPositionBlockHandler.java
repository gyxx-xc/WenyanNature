package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.WenyanDouble;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import net.minecraft.core.Direction;

public class SelfPositionBlockHandler implements JavacallHandler {
    private final Direction direction;

    public SelfPositionBlockHandler(Direction direction) {
        this.direction = direction;
    }

    @Override
    public WenyanValue handle(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof BlockRunner runner) {
            return new WenyanDouble(switch (direction) {
                case DOWN -> runner.getBlockPos().getCenter().y - context.holder().position().y;
                case UP -> context.holder().position().y - runner.getBlockPos().getCenter().y;
                case WEST -> runner.getBlockPos().getCenter().x - context.holder().position().x;
                case EAST -> context.holder().position().x - runner.getBlockPos().getCenter().x;
                case SOUTH -> runner.getBlockPos().getCenter().z - context.holder().position().z;
                case NORTH -> context.holder().position().z - runner.getBlockPos().getCenter().z;
            });
        } else {
            // TODO thr
            return WenyanNull.NULL;
        }
    }
    @Override
    public boolean isLocal(JavacallContext context) {
        return false;
    }
}
