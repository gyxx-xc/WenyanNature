package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.primitive.WenyanString;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * since a function can be a bytecode or a builtin function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface IWenyanFunction extends IWenyanValue {
    WenyanType<IWenyanFunction> TYPE = new WenyanType<>(JudouTypeText.Function.string(), IWenyanFunction.class);

    void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList,
                        Consumer<IWenyanValue> onReturn) throws WenyanException;

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    default <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(this.toString());
        }
        return null;
    }
}
