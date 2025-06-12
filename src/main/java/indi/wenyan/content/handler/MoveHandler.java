package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.phys.Vec3;

public class MoveHandler implements JavacallHandler {
    public final HandRunnerEntity entity;
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};

    public MoveHandler(HandRunnerEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        Object[] newArgs = JavacallHandlers.getArgs(args, ARGS_TYPE);
        newArgs[0] = Math.max(-20, Math.min(20, (double) newArgs[0]));
        newArgs[1] = Math.max(-20, Math.min(20, (double) newArgs[1]));
        newArgs[2] = Math.max(-20, Math.min(20, (double) newArgs[2]));
        entity.setDeltaMovement(new Vec3((double) newArgs[0]/10, (double) newArgs[1]/10, (double) newArgs[2]/10));
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
