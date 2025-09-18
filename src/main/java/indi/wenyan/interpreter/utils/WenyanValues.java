package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.structure.values.warper.WenyanBlock;
import indi.wenyan.interpreter.structure.values.warper.WenyanEntity;
import indi.wenyan.interpreter.structure.values.warper.WenyanList;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public enum WenyanValues {;
    public static IWenyanValue of(int i) {
        return new WenyanInteger(i);
    }

    public static IWenyanValue of(double l) {
        return new WenyanDouble(l);
    }

    public static IWenyanValue of(boolean b) {
        return new WenyanBoolean(b);
    }

    public static IWenyanValue of(String s) {
        return new WenyanString(s);
    }

    public static IWenyanValue of(List<IWenyanValue> l) {
        return new WenyanList(l);
    }

    public static IWenyanValue of(IWenyanValue... l) {
        return WenyanValues.of(List.of(l));
    }

    public static IWenyanValue of(Vec3 v) {
        return new WenyanVec3(v);
    }

    public static IWenyanValue of(BlockState b) {
        return new WenyanBlock(b);
    }

    public static IWenyanValue of(Entity e) {
        return new WenyanEntity(e);
    }

    public static boolean checkArgsType(List<IWenyanValue> args, WenyanType<?>... types) {
        if (args.size() != types.length) return false;
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).is(types[i])) return false;
        }
        return true;
    }
}
