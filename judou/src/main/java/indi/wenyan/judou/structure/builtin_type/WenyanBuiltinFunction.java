package indi.wenyan.judou.structure.builtin_type;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.exec.ICrossFunctionExecutable;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanFunction;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanLeftValue;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.ParsableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.function.Consumer;

/**
 * A built-in function implementation for Wenyan.
 * Represents a function created in Wenyan code.
 */
public record WenyanBuiltinFunction(
        IWenyanBytecode bytecode, List<WenyanBuiltinFunction.Arg> args,
        @Nullable List<IWenyanValue> refs) implements IWenyanFunction, ICrossFunctionExecutable {
    public static final WenyanType<WenyanBuiltinFunction> TYPE = new WenyanType<>(JudouTypeText.BuiltinFunction.string(), WenyanBuiltinFunction.class);

    @Override
    public void callWithReturn(IWenyanValue self, @UnknownNullability IWenyanRunner thread,
                               List<IWenyanValue> argsList, Consumer<IWenyanValue> onReturn)
            throws WenyanException {
        WenyanFrame newRuntime = getNewRuntime(self, argsList, thread.getCurrentRuntime(),
                (runner, returnValue) -> {
                    runner.getFrameManager().ret();
                    onReturn.accept(returnValue);
                });
        thread.getFrameManager().call(newRuntime);
    }

    /// return a new runtime with ready-to-use variables for the function
    public @NotNull WenyanFrame getNewRuntime(IWenyanValue self,
                                              @NotNull List<IWenyanValue> argsList,
                                              @Nullable WenyanFrame returnRuntime,
                                              WenyanFrame.ReturnBehavior onReturn)
            throws WenyanException {
        if (args().size() != argsList.size())
            throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(args().size(), argsList.size()));
        if (refs() == null)
            throw new WenyanException(JudouExceptionText.FunctionDoesNotHaveReferences.string());

        WenyanFrame newRuntime = new WenyanFrame(bytecode(), refs(), returnRuntime, onReturn);
        int i = 0;
        if (self != null) {
            newRuntime.setLocal(i++, self);
            newRuntime.setLocal(i++, self.as(WenyanBuiltinObject.TYPE).getObjectType().getParent());
        }
        int size = argsList.size();
        for (; i < size; i++)
            newRuntime.setLocal(i, WenyanLeftValue.varOf(
                    argsList.get(i).as(args().get(i).type().getType())));
        return newRuntime;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(JudouTypeText.Function.string());
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
    public record Arg(ParsableType type, String id) {
        @Override
        public @NotNull String toString() {
            return id + ":" + type.toString();
        }
    }
}
