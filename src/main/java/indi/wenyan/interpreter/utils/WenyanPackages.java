package indi.wenyan.interpreter.utils;

import java.util.HashMap;
import java.util.Map;

public class WenyanPackages {
    public static final WenyanFunctionEnvironment WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function("加", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.add(args[i]);
                }
                return value;
            })
            .function("減", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.sub(args[i]);
                }
                return value;
            })
            .function("乘", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.mul(args[i]);
                }
                return value;
            })
            .function("除", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.div(args[i]);
                }
                return value;
            })
            .function("變", args -> args[0].not())
            .function("銜", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.add(args[i]);
                }
                return value;
            })
            .function("充", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException("number of arguments does not match");
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.append(args[i]);
                }
                return value;
            })
            .build();

    public static final Map<String, WenyanFunctionEnvironment> PACKAGES = new HashMap<>();
}
