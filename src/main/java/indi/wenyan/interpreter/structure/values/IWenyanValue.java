package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.IWenyanComparable;
import indi.wenyan.interpreter.structure.IWenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.structure.values.warper.WenyanArrayList;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IWenyanValue {

    WenyanType<?> type();

    @Nullable
    default <T extends IWenyanValue> T casting(WenyanType<T> type) {
        return null;
    }

    @SuppressWarnings("unchecked")
    default <T extends IWenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type.tClass.isInstance(this)) {
            return (T) this;
        }
        if (casting(type) != null) {
            return casting(type);
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    default boolean is(WenyanType<?> type) {
        try {
            as(type);
            return true;
        } catch (WenyanException.WenyanTypeException e) {
            return false;
        }
    }

    static IWenyanValue add(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends IWenyanComputable> addType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(addType);
        IWenyanComputable right = other.as(addType);
        return left.add(right);
    }

    static IWenyanValue sub(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends IWenyanComputable> subType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(subType);
        IWenyanComputable right = other.as(subType);
        return left.subtract(right);
    }

    static IWenyanValue mul(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends IWenyanComputable> mulType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(mulType);
        IWenyanComputable right = other.as(mulType);
        return left.multiply(right);
    }

    static IWenyanValue div(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends IWenyanComputable> divType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(divType);
        IWenyanComputable right = other.as(divType);
        return left.divide(right);
    }

    static WenyanInteger mod(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanInteger left = self.as(WenyanInteger.TYPE);
        WenyanInteger right = other.as(WenyanInteger.TYPE);
        return left.mod(right);
    }

    static boolean equals(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        if (self.type() == WenyanDouble.TYPE || other.type() == WenyanDouble.TYPE) {
            return self.as(WenyanDouble.TYPE).equals(other.as(WenyanDouble.TYPE));
        }
        return self.equals(other);
    }

    static int compareTo(IWenyanValue self, IWenyanValue other) throws WenyanException.WenyanThrowException {
        WenyanType<? extends IWenyanComparable> cmpType = WenyanType.compareWiderType(self.type(), other.type());
        IWenyanComparable left = self.as(cmpType);
        IWenyanComparable right = other.as(cmpType);
        return left.compareTo(right);
    }

    static IWenyanValue emptyOf(WenyanType<?> type) {
        if (type == WenyanDouble.TYPE) return new WenyanDouble(0.0);
        if (type == WenyanBoolean.TYPE) return new WenyanBoolean(false);
        if (type == WenyanString.TYPE) return new WenyanString("");
        if (type == WenyanArrayList.TYPE) return new WenyanArrayList(new ArrayList<>());
        throw new WenyanException("unreached");
    }
}
