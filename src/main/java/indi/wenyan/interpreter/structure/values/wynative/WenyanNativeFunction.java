package indi.wenyan.interpreter.structure.values.wynative;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanLeftValue;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WenyanNativeFunction(List<Arg> args, WenyanBytecode bytecode) implements IWenyanFunction {
    public static final WenyanType<WenyanNativeFunction> TYPE = new WenyanType<>("native_function", WenyanNativeFunction.class);

    @Override
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        if (args().size() != argsList.size())
            throw new WenyanException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());

        WenyanRuntime newRuntime = new WenyanRuntime(bytecode);
        if (self != null) {
            newRuntime.setVariable(WenyanDataParser.SELF_ID, self);
            newRuntime.setVariable(WenyanDataParser.PARENT_ID,
                    self.as(WenyanNativeObject.TYPE).getObjectType().getParent());
        }
        for (int i = 0; i < argsList.size(); i++)
            newRuntime.setVariable(args.get(i).id(), WenyanLeftValue.varOf(argsList.get(i).as(args().get(i).type())));
        thread.call(newRuntime);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Component.translatable("type.wenyan_programming.function").getString());
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

    public record Arg(WenyanType<?> type, String id){
        @Override
        public @NotNull String toString() {
            return id + ":" + type.toString();
        }
    }
}
