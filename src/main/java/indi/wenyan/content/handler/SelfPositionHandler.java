package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3Object;
import net.minecraft.world.phys.Vec3;

public class SelfPositionHandler implements IExecCallHandler {

    @Override
    public IWenyanValue handle(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof HandRunnerEntity runner) {
            Vec3 vec = runner.position().subtract(context.holder().position());
            return new WenyanVec3Object(vec);
        } else {
            return WenyanNull.NULL;
        }
    }
}
