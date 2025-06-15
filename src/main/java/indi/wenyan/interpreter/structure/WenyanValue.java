package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public interface WenyanValue {
    WenyanNativeValue NULL = new WenyanNativeValue(WenyanType.NULL, null, true);
    HashMap<WenyanType, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(WenyanType.STRING, 0);
        put(WenyanType.LIST, 1);
        put(WenyanType.FUNCTION, 1);
        put(WenyanType.OBJECT, 1);
        put(WenyanType.OBJECT_TYPE, 1);
        put(WenyanType.DOUBLE, 2);
        put(WenyanType.INT, 3);
        put(WenyanType.BOOL, 4);
    }};

    WenyanType type();

    record FunctionSign(String name, WenyanType[] argTypes, WenyanFunction function) {}
}
