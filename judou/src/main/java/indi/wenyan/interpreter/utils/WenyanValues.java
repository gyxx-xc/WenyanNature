package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.structure.values.warper.WenyanList;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;

public enum WenyanValues {;
    public static IWenyanValue of() {
        return WenyanNull.NULL;
    }

    public static IWenyanValue of(long l) {
        return WenyanInteger.valueOf(l);
    }

    public static IWenyanValue of(@NotNull BigInteger bi) {
        return WenyanInteger.valueOf(bi);
    }

    public static IWenyanValue of(double d) {
        return new WenyanDouble(d);
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

    public static boolean checkArgsType(List<IWenyanValue> args, WenyanType<?>... types) {
        if (args.size() != types.length) return false;
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).is(types[i])) return false;
        }
        return true;
    }
}
