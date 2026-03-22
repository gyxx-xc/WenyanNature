package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.judou.utils.language.LanguageManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

/**
 * A built-in function implementation for Wenyan.
 * Represents a function created in Wenyan code.
 */
public record WenyanBuiltinFunction(
        WenyanBytecode bytecode, List<WenyanBuiltinFunction.Arg> args, @Nullable List<IWenyanValue> refs) implements IWenyanFunction {
    public static final WenyanType<WenyanBuiltinFunction> TYPE = new WenyanType<>("builtin_function", WenyanBuiltinFunction.class);

    @Override
    public void call(IWenyanValue self, @UnknownNullability IWenyanRunner thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        WenyanFrame newRuntime = getNewRuntime(self, argsList, thread.getCurrentRuntime());
        thread.call(newRuntime);
    }

    public @NotNull WenyanFrame getNewRuntime(IWenyanValue self, List<IWenyanValue> argsList, @Nullable WenyanFrame returnRuntime) throws WenyanException {
        if (args().size() != argsList.size())
            throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(args().size(), argsList.size()));
        if (refs == null)
            throw new WenyanException(JudouExceptionText.FunctionDoesNotHaveReferences.string());

        WenyanFrame newRuntime = new WenyanFrame(bytecode(), refs(), returnRuntime);
        int i = 0;
        if (self != null) {
            newRuntime.setLocal(i ++, self);
            newRuntime.setLocal(i ++, self.as(WenyanBuiltinObject.TYPE).getObjectType().getParent());
        }
        int size = argsList.size();
        for (; i < size; i++)
            newRuntime.setLocal(i, WenyanLeftValue.varOf(
                    argsList.get(i).as(args().get(i).type())));
        return newRuntime;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.getTranslation("type.wenyan_programming.function"));
        sb.append("(");
        int size = args().size();
        for (int i = 0; i < size; i++) {
            sb.append(args().get(i).toString());
            if (i < size - 1) {
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
