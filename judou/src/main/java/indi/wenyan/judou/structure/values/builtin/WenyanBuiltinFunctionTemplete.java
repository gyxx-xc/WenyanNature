package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.LanguageManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// i.e. WenyanCode
public record WenyanBuiltinFunctionTemplete(List<WenyanBuiltinFunction.Arg> args,
                                            WenyanBytecode bytecode) implements IWenyanValue {
    public static final WenyanType<WenyanBuiltinFunctionTemplete> TYPE = new WenyanType<>("builtin_function_templete", WenyanBuiltinFunctionTemplete.class);

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
}
