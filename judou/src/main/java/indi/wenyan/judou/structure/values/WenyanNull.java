package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.utils.language.JudouTypeText;

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
