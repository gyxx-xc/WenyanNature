package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouTypeText;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * since a function can be a bytecode or a builtin function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface IWenyanFunction extends IWenyanValue {
    WenyanType<IWenyanFunction> TYPE = new WenyanType<>(JudouTypeText.Function.string(), IWenyanFunction.class);

    void call(@Nullable IWenyanValue self, IWenyanRunner thread,
              List<IWenyanValue> argsList)
            throws WenyanException;

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
