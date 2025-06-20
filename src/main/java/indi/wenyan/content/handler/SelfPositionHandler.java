package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class SelfPositionHandler implements JavacallHandler {
    public SelfPositionHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        if (context.runnerWarper().runner() instanceof HandRunnerEntity runner) {
            Vec3 vec = runner.position().subtract(context.holder().position());
            return new WenyanNativeValue(
                    WenyanType.OBJECT,
                    new WenyanVec3Object(vec), true);
        } else {
            return WenyanValue.NULL;
        }
    }
    @Override
    public boolean isLocal(JavacallContext context) {
        return false;
    }
}
