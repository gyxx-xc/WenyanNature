package indi.wenyan.handler;

import indi.wenyan.entity.BulletEntity;
import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static indi.wenyan.WenyanNature.LEVEL_LOCK;

public class BulletHandler extends JavacallHandler {
    public final Level level;
    public final Vec3 pos;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.INT};

    public BulletHandler(Level level, Vec3 pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) {
        try {
            LEVEL_LOCK.acquire();
        } catch (InterruptedException e) {
            throw new WenyanException(e.toString());
        }
        Vec3 dir = new Vec3((double)args[0].getValue(), (double)args[1].getValue(), (double)args[2].getValue());
        BulletEntity bullet = new BulletEntity(level, pos, dir, 0.1, (int)args[3].getValue());
        level.addFreshEntity(bullet);
        LEVEL_LOCK.release();
        return null;
    }
}
