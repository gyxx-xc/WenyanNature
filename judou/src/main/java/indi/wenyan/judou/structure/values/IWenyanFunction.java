package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * since a function can be a bytecode or a builtin function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface IWenyanFunction extends IWenyanValue {
    WenyanType<IWenyanFunction> TYPE = new WenyanType<>("function", IWenyanFunction.class);

    void call(@Nullable IWenyanValue self, WenyanThread thread,
              List<IWenyanValue> argsList)
            throws WenyanThrowException;

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    default <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(this.toString());
        }
        return null;
    }
}
