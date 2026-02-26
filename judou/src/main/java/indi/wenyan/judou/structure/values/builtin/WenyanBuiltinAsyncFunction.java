package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.WenyanDataParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Deprecated // not going to do until runtime refactored
public record WenyanBuiltinAsyncFunction(List<Arg> args, WenyanBytecode bytecode) implements IWenyanFunction {
    public static final WenyanType<WenyanBuiltinAsyncFunction> TYPE = new WenyanType<>("builtin_function", WenyanBuiltinAsyncFunction.class);

    @Override
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        if (args().size() != argsList.size())
            throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));

        WenyanRuntime newRuntime = new WenyanRuntime(bytecode);
        if (self != null) {
            newRuntime.setVariable(WenyanDataParser.SELF_ID, self);
            newRuntime.setVariable(WenyanDataParser.PARENT_ID,
                    self.as(WenyanBuiltinObject.TYPE).getObjectType().getParent());
        }
        for (int i = 0; i < argsList.size(); i++)
            newRuntime.setVariable(args.get(i).id(), WenyanLeftValue.varOf(argsList.get(i).as(args().get(i).type())));
        thread.call(newRuntime);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LanguageManager.getTranslation("type.wenyan_programming.function"));
        sb.append("(");
        for (int i = 0; i < args().size(); i++) {
            sb.append(args().get(i).toString());
            if (i < args().size() - 1) {
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
