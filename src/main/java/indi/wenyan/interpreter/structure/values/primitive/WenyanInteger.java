package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanComparable;
import indi.wenyan.interpreter.structure.values.IWenyanComputable;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.ChineseUtils;
import indi.wenyan.interpreter.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

/**
 * Represents an integer value in Wenyan language.
 * Supports arithmetic operations and comparisons.
 */
public final class WenyanInteger implements IWenyanWarperValue<Integer>, IWenyanComputable, IWenyanComparable {
    private final BigInteger value;

    private WenyanInteger(BigInteger value) {
        this.value = value;
    }

    // in most cases, int is enough; and is much better for other to deal with
    @Override
    public Integer value() {
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            throw new WenyanException("Integer overflow");
        }
    }

    public static WenyanInteger valueOf(long i) {
        return i >= IntegerCache.low && i <= IntegerCache.high ? IntegerCache.cache[(int) (i + 128)] : new WenyanInteger(BigInteger.valueOf(i));
    }

    public static WenyanInteger valueOf(@NotNull BigInteger i) {
        // not checking cache, since it already alloc the space for bigint
        return new WenyanInteger(i);
    }

    public static final WenyanType<WenyanInteger> TYPE = new WenyanType<>("int", WenyanInteger.class);

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return WenyanValues.of(value.add(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return WenyanValues.of(value.subtract(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return WenyanValues.of(value.multiply(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) throws WenyanException.WenyanTypeException {
        WenyanInteger divisor = other.as(TYPE);
        if (divisor.value.equals(BigInteger.ZERO)) {
            throw new WenyanException("Division by zero");
        }
        return WenyanValues.of(value.divide(divisor.value));
    }

    public WenyanInteger mod(WenyanInteger other) {
        if (other.value.equals(BigInteger.ZERO)) {
            throw new WenyanException("Modulo by zero");
        }
        return new WenyanInteger(value.mod(other.value.abs()));
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanDouble.TYPE) {
            return (T) WenyanValues.of(value.doubleValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(toString());
        }
        if (type == WenyanBoolean.TYPE) {
            return (T) WenyanValues.of(!value.equals(BigInteger.ZERO));
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull IWenyanValue value) throws WenyanException.WenyanTypeException {
        return this.value.compareTo(value.as(TYPE).value);
    }

    @Override
    public @NotNull String toString() {
        return ChineseUtils.toChinese(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IWenyanValue wenyanValue) {
            try {
                return value.equals(wenyanValue.as(TYPE).value);
            } catch (WenyanException.WenyanTypeException ignored) {} // go outside
        }
        return false;
    }

    // since bigint cache only has 37 values, not good for int
    // cache copy from java.lang.Integer, and del some jdk magic
    private static final class IntegerCache {
        static final int low = -128;
        static final int high = 256;
        static final WenyanInteger[] cache;

        static {
            int size = high - low + 1;
            cache = new WenyanInteger[size];
            int j = -128;

            for (int i = 0; i < size; ++i) {
                cache[i] = new WenyanInteger(BigInteger.valueOf(j++));
            }
        }
    }
}
