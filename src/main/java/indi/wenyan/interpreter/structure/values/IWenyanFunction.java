package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * since a function can be a bytecode or a native function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface IWenyanFunction extends IWenyanValue {
    WenyanType<IWenyanFunction> TYPE = new WenyanType<>("function", IWenyanFunction.class);

    void call(IWenyanValue self, WenyanThread thread,
              List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    default <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(this.toString());
        }
        return null;
    }
}
