package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanComparable;
import indi.wenyan.interpreter.structure.WenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

public record WenyanString(String value) implements WenyanComputable {
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
    public WenyanValue add(WenyanValue other) throws WenyanException.WenyanThrowException {
        return new WenyanString(value+ other.as(TYPE).value);
    }

    @Override
    public WenyanValue subtract(WenyanValue other) {
        throw new WenyanException("");
    }

    @Override
    public WenyanValue multiply(WenyanValue other) {
        throw new WenyanException("");
    }

    @Override
    public WenyanValue divide(WenyanValue other) {
        throw new WenyanException("");
    }
}
