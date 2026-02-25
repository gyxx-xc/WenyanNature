package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.primitive.WenyanDouble;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.structure.values.warper.WenyanList;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface for all Wenyan language values
 */
public interface IWenyanValue {

    /**
     * @return The type of this value
     */
    WenyanType<?> type();

    /**
     * Attempts to cast this value to the specified type
     *
     * @param type Target type
     * @param <T>  Type parameter
     * @return Casted value or null if casting is not supported
     */
    @Nullable
    default <T extends IWenyanValue> T casting(WenyanType<T> type) {
        return null;
    }

    /**
     * Casts this value to the specified type or throws exception
     *
     * @param type Target type
     * @param <T>  Type parameter
     * @return Casted value
     * @throws WenyanException.WenyanTypeException If casting fails
     */
    @SuppressWarnings("unchecked")
    default <T extends IWenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type.tClass.isInstance(this)) {
            return (T) this;
        }
        if (casting(type) != null) {
            return casting(type);
        } else if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(toString());
        }
        throw new WenyanException.WenyanTypeException(LanguageManager.getTranslation("error.wenyan_programming.cannot_cast_") +
                type() + LanguageManager.getTranslation("error.wenyan_programming._to_") + type);
    }

    /**
     * Checks if this value can be cast to the specified type
     *
     * @param type Target type
     * @return True if this value can be cast to the target type
     */
    default boolean is(WenyanType<?> type) {
        try {
            as(type);
            return true;
        } catch (WenyanException.WenyanTypeException e) {
            return false;
        }
    }

    /**
     * Attempts to cast this value to the specified type and returns an Optional
     *
     * @param type Target type
     * @param <T>  Type parameter
     * @return Optional containing the casted value or empty if casting fails
     */
    default <T extends IWenyanValue> Optional<T> tryAs(WenyanType<T> type) {
        try {
            return Optional.of(as(type));
        } catch (WenyanException.WenyanTypeException e) {
            return Optional.empty();
        }
    }

    /**
     * Adds two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return Result of addition
     * @throws WenyanException If addition fails
     */
    static IWenyanValue add(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanType<? extends IWenyanComputable> addType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(addType);
        IWenyanComputable right = other.as(addType);
        return left.add(right);
    }

    /**
     * Subtracts two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return Result of subtraction
     * @throws WenyanException If subtraction fails
     */
    static IWenyanValue sub(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanType<? extends IWenyanComputable> subType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(subType);
        IWenyanComputable right = other.as(subType);
        return left.subtract(right);
    }

    /**
     * Multiplies two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return Result of multiplication
     * @throws WenyanException If multiplication fails
     */
    static IWenyanValue mul(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanType<? extends IWenyanComputable> mulType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(mulType);
        IWenyanComputable right = other.as(mulType);
        return left.multiply(right);
    }

    /**
     * Divides two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return Result of division
     * @throws WenyanException If division fails
     */
    static IWenyanValue div(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanType<? extends IWenyanComputable> divType = WenyanType.computeWiderType(self.type(), other.type());
        IWenyanComputable left = self.as(divType);
        IWenyanComputable right = other.as(divType);
        return left.divide(right);
    }

    /**
     * Calculates modulus of two Wenyan integers
     *
     * @param self  First value
     * @param other Second value
     * @return Result of modulus operation
     * @throws WenyanException If operation fails
     */
    static WenyanInteger mod(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanInteger left = self.as(WenyanInteger.TYPE);
        WenyanInteger right = other.as(WenyanInteger.TYPE);
        return left.mod(right);
    }

    /**
     * Checks equality between two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return True if values are equal
     * @throws WenyanException If comparison fails
     */
    static boolean equals(IWenyanValue self, IWenyanValue other) throws WenyanException {
        if (self instanceof WenyanLeftValue leftValue) self = leftValue.value;
        if (other instanceof WenyanLeftValue leftValue) other = leftValue.value;
        if (self.type() == WenyanDouble.TYPE || other.type() == WenyanDouble.TYPE) {
            return self.as(WenyanDouble.TYPE).equals(other.as(WenyanDouble.TYPE));
        }
        return self.equals(other);
    }

    /**
     * Compares two Wenyan values
     *
     * @param self  First value
     * @param other Second value
     * @return Comparison result (negative, zero, positive)
     * @throws WenyanException If comparison fails
     */
    static int compareTo(IWenyanValue self, IWenyanValue other) throws WenyanException {
        WenyanType<? extends IWenyanComparable> cmpType = WenyanType.compareWiderType(self.type(), other.type());
        IWenyanComparable left = self.as(cmpType);
        IWenyanComparable right = other.as(cmpType);
        return left.compareTo(right);
    }

    /**
     * Creates an empty value of the specified type
     *
     * @param type Target type
     * @return Empty value of the specified type
     */
    static IWenyanValue emptyOf(WenyanType<?> type) throws WenyanException {
        if (type == WenyanDouble.TYPE) return WenyanValues.of(0.0);
        if (type == WenyanBoolean.TYPE) return WenyanValues.of(false);
        if (type == WenyanString.TYPE) return WenyanValues.of("");
        if (type == WenyanList.TYPE) return new WenyanList();
        throw new WenyanUnreachedException();
    }
}
