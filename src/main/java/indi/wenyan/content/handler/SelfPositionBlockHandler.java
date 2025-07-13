package indi.wenyan.content.handler;

import indi.wenyan.content.block.RunnerBlockEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.core.Direction;

public class SelfPositionBlockHandler implements IExecCallHandler {
    private final Direction direction;

    public SelfPositionBlockHandler(Direction direction) {
        this.direction = direction;
    }

    @Override
    public IWenyanValue handle(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof RunnerBlockEntity runner) {
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
}
