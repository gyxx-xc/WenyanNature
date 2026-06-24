package indi.wenyan.interpreter_impl.value;

import indi.wenyan.content.block.runner.IWenyanPackageable;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.IWenyanObject;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;

public record WenyanCodeWithExecutor(IWenyanPackageable packageable) implements IWenyanObject {
    public static final WenyanType<WenyanCodeWithExecutor> TYPE = new WenyanType<>(JudouTypeText.CodeExecutor.string(), WenyanCodeWithExecutor.class);

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        throw new WenyanUnreachedException();
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
