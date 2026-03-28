package indi.wenyan.judou.utils.function;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;

public enum WenyanValues {;
    public static WenyanNull of() {
        return WenyanNull.NULL;
    }

    public static WenyanInteger of(long l) {
        return WenyanInteger.valueOf(l);
    }

    public static WenyanInteger of(@NotNull BigInteger bi) {
        return WenyanInteger.valueOf(bi);
    }

    public static WenyanDouble of(double d) {
        return new WenyanDouble(d);
    }

    public static WenyanBoolean of(boolean b) {
        return b ? WenyanBoolean.TRUE : WenyanBoolean.FALSE;
    }

    public static WenyanString of(String s) {
        return new WenyanString(s);
    }

    public static WenyanList of(List<IWenyanValue> l) {
        return new WenyanList(l);
    }

    public static WenyanList of(IWenyanValue... l) {
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
