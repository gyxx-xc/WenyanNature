package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.*;
import net.minecraft.world.phys.Vec3;

public class SelfPositionHandler implements JavacallHandler {

    @Override
    public WenyanValue handle(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof HandRunnerEntity runner) {
            Vec3 vec = runner.position().subtract(context.holder().position());
            return new WenyanVec3Object(vec);
        } else {
            return WenyanNull.NULL;
        }
    }
}
