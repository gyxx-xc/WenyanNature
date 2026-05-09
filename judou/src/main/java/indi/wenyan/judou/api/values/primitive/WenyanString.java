package indi.wenyan.judou.api.values.primitive;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.values.IWenyanComputable;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.IWenyanWarperValue;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.JudouTypeText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string value in Wenyan language.
 * Supports concatenation through addition operation.
 */
public record WenyanString(String value)
        implements IWenyanWarperValue<String>, IWenyanComputable {
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>(JudouTypeText.String.string(), WenyanString.class);

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
    public IWenyanValue add(IWenyanValue other) throws WenyanException {
        return WenyanValues.of(value+ other.as(TYPE).value);
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) throws WenyanException {
        throw new WenyanException(JudouExceptionText.OperationNotSupported.string());
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) throws WenyanException {
        throw new WenyanException(JudouExceptionText.OperationNotSupported.string());
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) throws WenyanException {
        throw new WenyanException(JudouExceptionText.OperationNotSupported.string());
    }
}
