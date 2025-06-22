package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public interface WenyanValue {
    WenyanNativeValue NULL = new WenyanNativeValue(WenyanType.NULL, null, true);
    HashMap<WenyanType<?>, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(WenyanType.STRING, 0);
        put(WenyanType.LIST, 1);
        put(WenyanType.FUNCTION, 1);
        put(WenyanType.OBJECT, 1);
        put(WenyanType.OBJECT_TYPE, 1);
        put(WenyanType.DOUBLE, 2);
        put(WenyanType.INT, 3);
        put(WenyanType.BOOL, 4);
    }};

    static WenyanValue add(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType<? extends WenyanComputable> addType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.As(addType);
        WenyanComputable right = other.As(addType);
        return left.add(right);
    }

    static WenyanValue sub(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType<? extends WenyanComputable> subType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.As(subType);
        WenyanComputable right = other.As(subType);
        return left.subtract(right);
    }

    static WenyanValue mul(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType<? extends WenyanComputable> mulType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.As(mulType);
        WenyanComputable right = other.As(mulType);
        return left.multiply(right);
    }

    static WenyanValue div(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType<? extends WenyanComputable> divType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.As(divType);
        WenyanComputable right = other.As(divType);
        return left.divide(right);
    }

    static WenyanNativeValue mod(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanNativeValue left = self.As(WenyanType.INT);
        WenyanNativeValue right = other.As(WenyanType.INT);
        return left.mod(right);
    }

    static boolean equals(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        return self.equals(other);
    }

    static int compareTo(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType<? extends WenyanComparable> cmpType = WenyanType.compareWiderType(self.type(), other.type());
        WenyanComparable left = self.As(cmpType);
        WenyanComparable right = other.As(cmpType);
        return left.compareTo(right);
    }

    WenyanType<?> type();

    <T extends WenyanValue> T As(WenyanType<T> type);

    record FunctionSign(String name, WenyanType<?>[] argTypes, WenyanFunction function) { }
}
