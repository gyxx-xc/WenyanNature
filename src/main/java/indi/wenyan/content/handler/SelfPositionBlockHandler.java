package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class SelfPositionBlockHandler implements JavacallHandler {
    private final Player holder;
    private final BlockRunner runner;
    private final Direction direction;

    public SelfPositionBlockHandler(Player holder, BlockRunner runner, Direction direction) {
        this.holder = holder;
        this.runner = runner;
        this.direction = direction;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        return new WenyanNativeValue(WenyanType.DOUBLE, switch (direction) {
            case DOWN -> runner.getBlockPos().getCenter().y - holder.position().y;
            case UP -> holder.position().y - runner.getBlockPos().getCenter().y;
            case WEST -> runner.getBlockPos().getCenter().x - holder.position().x;
            case EAST -> holder.position().x - runner.getBlockPos().getCenter().x;
            case SOUTH -> runner.getBlockPos().getCenter().z - holder.position().z;
            case NORTH -> holder.position().z - runner.getBlockPos().getCenter().z;
        }, true);
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
