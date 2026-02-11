package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.LanguageManager;

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
