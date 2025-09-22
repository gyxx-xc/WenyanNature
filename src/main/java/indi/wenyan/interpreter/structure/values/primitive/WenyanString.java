package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanComputable;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a string value in Wenyan language.
 * Supports concatenation through addition operation.
 */
public record WenyanString(String value)
        implements IWenyanWarperValue<String>, IWenyanComputable {
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>("string", WenyanString.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        return value;
    }

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanThrowException {
        return WenyanValues.of(value+ other.as(TYPE).value);
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) {
        throw new WenyanException("");
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) {
        throw new WenyanException("");
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) {
        throw new WenyanException("");
    }
}
