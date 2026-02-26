package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;

@Deprecated // not going to do until runtime refactored
public class WenyanFuture implements IWenyanValue {
    public static final WenyanType<WenyanFuture> TYPE = new WenyanType<>("iterator", WenyanFuture.class);
    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
