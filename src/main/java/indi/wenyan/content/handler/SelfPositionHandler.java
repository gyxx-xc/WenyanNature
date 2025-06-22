package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.WenyanNativeValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanObject;
import indi.wenyan.interpreter.structure.values.WenyanVec3Object;
import net.minecraft.world.phys.Vec3;

public class SelfPositionHandler implements JavacallHandler {
    public SelfPositionHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        if (context.runnerWarper().runner() instanceof HandRunnerEntity runner) {
            Vec3 vec = runner.position().subtract(context.holder().position());
            return new WenyanNativeValue(
                    WenyanObject.TYPE,
                    new WenyanVec3Object(vec), true);
        } else {
            return WenyanNull.NULL;
        }
    }
    @Override
    public boolean isLocal(JavacallContext context) {
        return false;
    }
}
