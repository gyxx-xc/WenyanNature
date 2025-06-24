package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * since a function can be a bytecode or a native function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface WenyanFunction extends WenyanValue {
    WenyanType<WenyanFunction> TYPE = new WenyanType<>("function", WenyanFunction.class);

    void call(WenyanValue self, WenyanThread thread,
              List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    default <T extends WenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(this.toString());
        }
        return null;
    }
}
