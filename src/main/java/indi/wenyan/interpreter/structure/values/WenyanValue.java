package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanComparable;
import indi.wenyan.interpreter.structure.WenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface WenyanValue {

    WenyanType<?> type();

    @Nullable
    default <T extends WenyanValue> T casting(WenyanType<T> type) {
        return null;
    }

    @SuppressWarnings("unchecked")
    default <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type.tClass.isInstance(this)) {
            return (T) this;
        }
        if (casting(type) != null) {
            return casting(type);
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

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
        if (type == WenyanDouble.TYPE) return new WenyanDouble(0.0);
        if (type == WenyanBoolean.TYPE) return new WenyanBoolean(false);
        if (type == WenyanString.TYPE) return new WenyanString("");
        if (type == WenyanArrayObject.TYPE) return new WenyanArrayObject(new ArrayList<>());
        throw new WenyanException("unreached");
    }
}
