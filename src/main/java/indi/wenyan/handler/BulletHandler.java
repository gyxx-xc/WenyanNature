package indi.wenyan.handler;

import indi.wenyan.entity.BulletEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BulletHandler extends JavacallHandler {
    public final Level level;
    public final Vec3 pos;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.INT};

    public BulletHandler(Level level, Vec3 pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanTypeException {
        Object[] newArgs = getArgs(args);
        Vec3 dir = new Vec3((double)newArgs[0], (double)newArgs[1], (double)newArgs[2]);
        BulletEntity bullet = new BulletEntity(level, pos, dir, (double) newArgs[3]/10, (int)newArgs[4]);
        level.addFreshEntity(bullet);
        return null;
    }

    private Object[] getArgs(WenyanValue[] args) throws WenyanException.WenyanTypeException {
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].casting(ARGS_TYPE[i]).getValue();
        return newArgs;
    }
}
