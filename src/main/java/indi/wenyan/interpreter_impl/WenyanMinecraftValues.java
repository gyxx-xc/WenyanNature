package indi.wenyan.interpreter_impl;

import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter_impl.value.WenyanBlock;
import indi.wenyan.interpreter_impl.value.WenyanEntity;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public enum WenyanMinecraftValues {;

    public static IWenyanValue of(Vec3 v) {
        return new WenyanVec3(v);
    }

    public static IWenyanValue of(BlockState b) {
        return new WenyanBlock(b);
    }

    public static IWenyanValue of(Entity e) {
        return new WenyanEntity(e);
    }
}
