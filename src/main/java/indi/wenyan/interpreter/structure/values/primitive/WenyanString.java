package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.IWenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

public record WenyanString(String value) implements IWenyanComputable {
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>("string", WenyanString.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanThrowException {
        return new WenyanString(value+ other.as(TYPE).value);
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
