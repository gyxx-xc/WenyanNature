package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanComputable;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string value in Wenyan language.
 * Supports concatenation through addition operation.
 */
public record WenyanString(String value)
        implements IWenyanWarperValue<String>, IWenyanComputable {
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>("string", WenyanString.class);

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanBoolean.TYPE) {
            return (T) WenyanValues.of(!value.isEmpty());
        }
        return null;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        return value;
    }

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanThrowException {
        return WenyanValues.of(value+ other.as(TYPE).value);
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) throws WenyanThrowException {
        throw new WenyanException("");
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) throws WenyanThrowException {
        throw new WenyanException("");
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) throws WenyanThrowException {
        throw new WenyanException("");
    }
}
