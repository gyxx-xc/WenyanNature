package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.*;

public interface WenyanValue {

    static WenyanValue add(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends WenyanComputable> addType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.as(addType);
        WenyanComputable right = other.as(addType);
        return left.add(right);
    }

    static WenyanValue sub(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends WenyanComputable> subType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.as(subType);
        WenyanComputable right = other.as(subType);
        return left.subtract(right);
    }

    static WenyanValue mul(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends WenyanComputable> mulType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.as(mulType);
        WenyanComputable right = other.as(mulType);
        return left.multiply(right);
    }

    static WenyanValue div(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends WenyanComputable> divType = WenyanType.computeWiderType(self.type(), other.type());
        WenyanComputable left = self.as(divType);
        WenyanComputable right = other.as(divType);
        return left.divide(right);
    }

    static WenyanInteger mod(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanInteger left = self.as(WenyanInteger.TYPE);
        WenyanInteger right = other.as(WenyanInteger.TYPE);
        return left.mod(right);
    }

    static boolean equals(WenyanValue self, WenyanValue other) throws WenyanException.WenyanTypeException {
        return self.equals(other);
    }

    static int compareTo(WenyanValue self, WenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends WenyanComparable> cmpType = WenyanType.compareWiderType(self.type(), other.type());
        WenyanComparable left = self.as(cmpType);
        WenyanComparable right = other.as(cmpType);
        return left.compareTo(right);
    }

    static WenyanValue emptyOf(WenyanType<?> type) throws WenyanException.WenyanTypeException {
        if (type == WenyanInteger.TYPE) return new WenyanInteger(0);
        if (type == WenyanDouble.TYPE) return new WenyanDouble(0.0);
        if (type == WenyanBoolean.TYPE) return new WenyanBoolean(false);
        if (type == WenyanString.TYPE) return new WenyanString("");
        throw new WenyanException("unreached");
    }

    WenyanType<?> type();

    <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException;

    void setValue(WenyanValue value) throws WenyanException.WenyanTypeException;
}
