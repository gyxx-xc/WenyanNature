package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class SelfPositionHandler extends JavacallHandler {
    private final Player holder;
    private final HandRunnerEntity runner;
    private final Direction direction;

    public SelfPositionHandler(Player holder, HandRunnerEntity runner, Direction direction) {
        this.holder = holder;
        this.runner = runner;
        this.direction = direction;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return new WenyanValue(WenyanValue.Type.DOUBLE, switch (direction) {
            case DOWN -> runner.position().y - holder.position().y;
            case UP -> holder.position().y - runner.position().y;
            case WEST -> runner.position().x - holder.position().x;
            case EAST -> holder.position().x - runner.position().x;
            case SOUTH -> runner.position().z - holder.position().z;
            case NORTH -> holder.position().z - runner.position().z;
        }, true);
    }
}
