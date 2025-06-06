package indi.wenyan.interpreter.handler;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandler;
import net.minecraft.network.chat.Component;

public class LocalCallHandler extends JavacallHandler {
    private final WenyanFunction function;

    public LocalCallHandler(WenyanFunction function) {
        this.function = function;
    }

    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return function.apply(args);
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    public static WenyanFunction withArgs(BiFunction function) {
        return args -> {
            if (args.length <= 1)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            WenyanValue value = args[0];
            for (int i = 1; i < args.length; i++) {
                value = function.apply(value, args[i]);
            }
            return value;
        };
    }

    @FunctionalInterface
    public interface BiFunction {
        WenyanValue apply(WenyanValue a, WenyanValue b) throws WenyanException.WenyanThrowException;
    }
}
