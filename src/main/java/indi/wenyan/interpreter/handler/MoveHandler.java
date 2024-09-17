package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.phys.Vec3;

public class MoveHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public static final WenyanValue.Type[] ARGS_TYPE = {WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE};

    public MoveHandler(HandRunnerEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        Object[] newArgs = getArgs(args, ARGS_TYPE);
        entity.setDeltaMovement(new Vec3((double) newArgs[0], (double) newArgs[1], (double) newArgs[2]));
        return null;
    }
}
