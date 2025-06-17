package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.*;
import net.minecraft.core.Direction;

public class SelfPositionBlockHandler implements JavacallHandler {
    private final Direction direction;

    public SelfPositionBlockHandler(Direction direction) {
        this.direction = direction;
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        if (context.runnerWarper().runner() instanceof BlockRunner runner) {
            return new WenyanNativeValue(WenyanType.DOUBLE, switch (direction) {
                case DOWN -> runner.getBlockPos().getCenter().y - context.holder().position().y;
                case UP -> context.holder().position().y - runner.getBlockPos().getCenter().y;
                case WEST -> runner.getBlockPos().getCenter().x - context.holder().position().x;
                case EAST -> context.holder().position().x - runner.getBlockPos().getCenter().x;
                case SOUTH -> runner.getBlockPos().getCenter().z - context.holder().position().z;
                case NORTH -> context.holder().position().z - runner.getBlockPos().getCenter().z;
            }, true);
        } else {
            // TODO thr
            return WenyanValue.NULL;
        }
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
