package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.world.phys.Vec3;

public class MoveHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE};

    public MoveHandler(HandRunnerEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        Object[] newArgs = getArgs(args, ARGS_TYPE);
        newArgs[0] = Math.max(-20, Math.min(20, (double) newArgs[0]));
        newArgs[1] = Math.max(-20, Math.min(20, (double) newArgs[1]));
        newArgs[2] = Math.max(-20, Math.min(20, (double) newArgs[2]));
        entity.setDeltaMovement(new Vec3((double) newArgs[0]/10, (double) newArgs[1]/10, (double) newArgs[2]/10));
        return null;
    }
}
