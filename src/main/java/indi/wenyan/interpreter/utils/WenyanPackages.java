package indi.wenyan.interpreter.utils;

import java.util.Map;

public class WenyanPackages {
    public static final Map<String, WenyanFunctionEnvironment> PACKAGES = Map.of(
        "wenyan", new WenyanFunctionEnvironment()
    );
}
