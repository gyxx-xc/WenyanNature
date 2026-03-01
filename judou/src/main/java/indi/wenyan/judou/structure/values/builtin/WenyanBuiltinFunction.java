package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.utils.LanguageManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A built-in function implementation for Wenyan.
 * Represents a function created in Wenyan code.
 */
public record WenyanBuiltinFunction(WenyanBuiltinFunctionTemplete functionTemplete, List<IWenyanValue> refs) implements IWenyanFunction {
    public static final WenyanType<WenyanBuiltinFunction> TYPE = new WenyanType<>("builtin_function", WenyanBuiltinFunction.class);

    @Override
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        if (functionTemplete.args().size() != argsList.size())
            throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));

        WenyanRuntime newRuntime = new WenyanRuntime(functionTemplete.bytecode(), refs());
        int i = 0;
        if (self != null) {
            newRuntime.getLocals().set(i ++, self);
            newRuntime.getLocals().set(i ++, self.as(WenyanBuiltinObject.TYPE).getObjectType().getParent());
        }
        for (; i < argsList.size(); i++)
            newRuntime.getLocals().set(i,
                    WenyanLeftValue.varOf(argsList.get(i).as(functionTemplete.args().get(i).type())));
        thread.call(newRuntime);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.getTranslation("type.wenyan_programming.function"));
        sb.append("(");
        for (int i = 0; i < functionTemplete.args().size(); i++) {
            sb.append(functionTemplete.args().get(i).toString());
            if (i < functionTemplete.args().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    /**
     * Represents a function argument with a type and identifier.
     */
    public record Arg(WenyanType<?> type, String id){
        @Override
        public @NotNull String toString() {
            return id + ":" + type.toString();
        }
    }
}
