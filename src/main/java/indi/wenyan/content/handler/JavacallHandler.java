package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.WenyanFunction;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public interface JavacallHandler extends WenyanFunction {
    WenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException;

    /**
     * Decided if this handler is running at program thread.
     * <p>
     * the handler will be executed in the main thread of MC if it is not local,
     * This is important since the MC is not thread-safe,
     * and can cause strange bug and unmatched exception, making it really hard to debug.
     *
     * @return true if local, false otherwise
     */
    default boolean isLocal(JavacallContext context) {
        return false;
    }

    /**
     * The step of this handler.
     * <p>
     * It can be decided by the feature of the handler for game balance,
     * e.g. powerful handler may take more time to execute.
     * However, it's better to keep the handler time not longer than O(step),
     * which may cause the program to be stuck.
     *
     * @return the step of this handler
     */
    @SuppressWarnings("unused")
    default int getStep(int args, WenyanThread thread) {
        return 1;
    }

    @Override
    default void call(WenyanValue self, WenyanThread thread,
                      List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException{
        JavacallContext context = new JavacallContext(thread.program.warper, self, argsList,
                thread, this, thread.program.holder);
        if (isLocal(context)) {
            context.thread().currentRuntime().processStack.push(handle(context));
        } else {
            thread.program.requestThreads.add(context);
            thread.block();
        }
    }

    static List<Object> getArgs(List<WenyanValue> args, WenyanType[] args_type)
            throws WenyanException.WenyanTypeException {
        List<Object> newArgs = new ArrayList<>();
        if (args.size() != args_type.length)
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < args.size(); i++)
            newArgs.add(args.get(i).casting(args_type[i]).getValue());
        return newArgs;
    }
}
