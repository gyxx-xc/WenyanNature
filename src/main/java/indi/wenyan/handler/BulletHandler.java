package indi.wenyan.handler;

import indi.wenyan.entity.BulletEntity;
import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BulletHandler extends JavacallHandler {
    public final Level level;
    public final HandRunnerEntity runner;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.DOUBLE, WenyanValue.Type.INT};

    public BulletHandler(Level level, HandRunnerEntity runner) {
        this.level = level;
        this.runner = runner;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) {
        Vec3 dir = new Vec3((double)args[0].getValue(), (double)args[1].getValue(), (double)args[2].getValue());
        BulletEntity bullet = new BulletEntity(level, runner.getEyePosition(), dir, 0.1, (int)args[3].getValue());
        level.addFreshEntity(bullet);
        return null;
    }
}
