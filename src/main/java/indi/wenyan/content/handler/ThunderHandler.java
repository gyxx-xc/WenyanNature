package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class ThunderHandler implements JavacallHandler {
    public ThunderHandler() {
        super();
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity) {
            Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
            e.moveTo(entity.getX(), entity.getY(), entity.getZ());
            entity.level().addFreshEntity(e);
        }
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
