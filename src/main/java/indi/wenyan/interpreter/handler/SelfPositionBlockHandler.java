package indi.wenyan.interpreter.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.parent.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class SelfPositionBlockHandler extends JavacallHandler {
    private final Player holder;
    private final BlockRunner runner;
    private final Direction direction;

    public SelfPositionBlockHandler(Player holder, BlockRunner runner, Direction direction) {
        this.holder = holder;
        this.runner = runner;
        this.direction = direction;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return new WenyanValue(WenyanValue.Type.DOUBLE, switch (direction) {
            case DOWN -> runner.getBlockPos().getCenter().y - holder.position().y;
            case UP -> holder.position().y - runner.getBlockPos().getCenter().y;
            case WEST -> runner.getBlockPos().getCenter().x - holder.position().x;
            case EAST -> holder.position().x - runner.getBlockPos().getCenter().x;
            case SOUTH -> runner.getBlockPos().getCenter().z - holder.position().z;
            case NORTH -> holder.position().z - runner.getBlockPos().getCenter().z;
        }, true);
    }
}
