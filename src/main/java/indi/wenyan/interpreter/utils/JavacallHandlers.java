package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public final class JavacallHandlers {
    private JavacallHandlers() {}

    public static Object[] getArgs(WenyanNativeValue[] args, WenyanType[] args_type) throws WenyanException.WenyanTypeException {
        Object[] newArgs = new Object[args.length];
        if (args.length != args_type.length)
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].casting(args_type[i]).getValue();
        return newArgs;
    }

    @FunctionalInterface
    public
    interface WenyanFunction {
        WenyanNativeValue apply(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException;
    }

    public record Request(
            WenyanThread thread,
            WenyanNativeValue[] args,
            boolean noReturn,
            JavacallHandler handler
    ) {
        public void handle() throws WenyanException.WenyanThrowException {
            handler.handle(thread, args, noReturn);
            thread.program.readyQueue.add(thread);
            thread.state = WenyanThread.State.READY;
        }
    }
}
