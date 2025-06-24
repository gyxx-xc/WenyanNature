package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

public class WenyanLeftValue implements IWenyanValue {
    public IWenyanValue value;

    public WenyanLeftValue(IWenyanValue value) {
        this.value = value;
    }

    public static IWenyanValue varOf(IWenyanValue value) {
        if (value instanceof WenyanLeftValue leftValue) {
            return leftValue;
        } else {
            return new WenyanLeftValue(value);
        }
    }

    @Override
    public WenyanType<?> type() {
        return value.type();
    }

    @Override
    public <T extends IWenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        // turn into right if casting
        return value.as(type);
    }

    public void setValue(IWenyanValue value) {
        this.value = value;
    }
}
