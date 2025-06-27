package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public enum JavacallHandlers {
    ;

    public static List<Object> getArgs(List<IWenyanValue> args, WenyanType<?>[] args_type)
            throws WenyanException.WenyanTypeException {
        List<Object> newArgs = new ArrayList<>();
        if (args.size() != args_type.length)
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < args.size(); i++)
            newArgs.add(args.get(i).as(args_type[i]));
        return newArgs;
    }
}
