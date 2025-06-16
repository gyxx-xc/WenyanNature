package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class JavacallHandlers {
    private JavacallHandlers() {}

    public static List<Object> getArgs(List<WenyanNativeValue> args, WenyanType[] args_type) throws WenyanException.WenyanTypeException {
        List<Object> newArgs = new ArrayList<>();
        if (args.size() != args_type.length)
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < args.size(); i++)
            newArgs.add(args.get(i).casting(args_type[i]).getValue());
        return newArgs;
    }

    @FunctionalInterface
    public
    interface WenyanFunction {
        WenyanNativeValue apply(List<WenyanNativeValue> args) throws WenyanException.WenyanThrowException;
    }
}
