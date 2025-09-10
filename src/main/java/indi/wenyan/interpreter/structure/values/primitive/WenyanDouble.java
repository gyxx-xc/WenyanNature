package indi.wenyan.interpreter.structure.values.primitive;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanComparable;
import indi.wenyan.interpreter.structure.values.IWenyanComputable;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a double-precision floating point value in Wenyan language.
 * Supports arithmetic operations and comparisons.
 */
public record WenyanDouble(Double value)
        implements IWenyanWarperValue<Double>, IWenyanComputable, IWenyanComparable {
    public static final WenyanType<WenyanDouble> TYPE = new WenyanType<>("double", WenyanDouble.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanInteger.TYPE) {
            return (T) new WenyanInteger(value.intValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        if (type == WenyanBoolean.TYPE) {
            return (T) new WenyanBoolean(value != 0);
        }
        return null;
    }

    @Override
    public WenyanDouble add(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value + other.as(TYPE).value);
    }

    @Override
    public WenyanDouble subtract(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value - other.as(TYPE).value);
    }

    @Override
    public WenyanDouble multiply(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value * other.as(TYPE).value);
    }

    @Override
    public WenyanDouble divide(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value / other.as(TYPE).value);
    }

    @Override
    public int compareTo(IWenyanValue value) throws WenyanException.WenyanTypeException {
        return this.value.compareTo(value.as(TYPE).value);
    }

    @Override
    public @NotNull String toString() {
        ULocale locale = ULocale.forLanguageTag("zh-Hant");
        NumberFormat formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
        // replace "點" with "又"
        return formatter.format(value).replace("點", "又");
    }
}
