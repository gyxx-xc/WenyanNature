package indi.wenyan.judou.api.utils;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.values.IWenyanFunction;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.primitive.*;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
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

    /// The function must be **thread safe**, use {@link IRequestCallHandler} otherwise
    public static IWenyanFunction of(BuiltinFunction function) {
        return new WenyanInlineJavacall(function);
    }

    public static boolean checkArgsType(List<IWenyanValue> args, WenyanType<?>... types) {
        if (args.size() != types.length) return false;
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).is(types[i])) return false;
        }
        return true;
    }

    /**
     * Functional interface for builtin function implementations.
     * Must be <b>thread safe</b>, use {@link IRequestCallHandler} otherwise
     */
    @FunctionalInterface
    public interface BuiltinFunction {
        /**
         * Applies the function to the given arguments.
         *
         * @param self the self value
         * @param args the function arguments
         * @return the result value
         * @throws WenyanException if an error occurs during function execution
         */
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args)
                throws WenyanException;
    }
}
