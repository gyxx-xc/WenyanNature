package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;

public enum WenyanNull implements IWenyanValue {
    NULL;

    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>(JudouTypeText.Null.string(), WenyanNull.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return JudouTypeText.Null.string();
    }
}
