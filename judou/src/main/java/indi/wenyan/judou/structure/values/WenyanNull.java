package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.utils.LanguageManager;

public enum WenyanNull implements IWenyanValue {
    NULL;

    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>("null", WenyanNull.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return LanguageManager.getTranslation("type.wenyan_programming.null");
    }
}
