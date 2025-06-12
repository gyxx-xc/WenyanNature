package indi.wenyan.content.handler;

import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BulletHandler implements JavacallHandler {
    public final Level level;
    public final HandRunnerEntity entity;
    public final Player holder;

    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.INT};

    public BulletHandler(Level level, HandRunnerEntity entity, Player holder) {
        this.level = level;
        this.entity = entity;
        this.holder = holder;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanTypeException {
        Object[] newArgs = JavacallHandlers.getArgs(args, ARGS_TYPE);
        newArgs[0] = Math.max(-10, Math.min(10, (double) newArgs[0]));
        newArgs[1] = Math.max(-10, Math.min(10, (double) newArgs[1]));
        newArgs[2] = Math.max(-10, Math.min(10, (double) newArgs[2]));
        newArgs[4] = Math.max(1, Math.min(200, (int) newArgs[4]));
        newArgs[3] = Math.max(1, Math.min(20, (double) newArgs[3]));
        Vec3 dir = new Vec3((double)newArgs[0], (double)newArgs[1], (double)newArgs[2]);
        BulletEntity bullet = new BulletEntity(level, entity.getPosition(0), dir, (double) newArgs[3]/10, (int)newArgs[4], holder);
        level.addFreshEntity(bullet);
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
