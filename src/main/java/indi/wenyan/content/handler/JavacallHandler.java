package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanFunction;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

import java.util.ArrayList;
import java.util.List;

public interface JavacallHandler extends WenyanFunction {
    WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException;

    /**
     * Decided if this handler is running at program thread.
     * <p>
     * the handler will be executed in the main thread of MC if it is not local,
     * This is important since the MC is not thread-safe,
     * and can cause strange bug and unmatched exception, making it really hard to debug.
     *
     * @return true if local, false otherwise
     */
    boolean isLocal();

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

    default void handleWarper(JavacallContext context) throws WenyanException.WenyanThrowException {
        WenyanNativeValue value = handle(context);
//        if (!context.isConstructor())
        context.thread().currentRuntime().processStack.push(value);
    }

    @Override
    default void call(WenyanNativeValue.FunctionSign sign, WenyanNativeValue self,
                      WenyanThread thread, int args, boolean isConstructor)
            throws WenyanException.WenyanThrowException{
        List<WenyanNativeValue> argsList = new ArrayList<>(args);
        if (self != null)
            argsList.add(self);
        WenyanRuntime runtime = thread.currentRuntime();
        for (int i = 0; i < args; i++)
            argsList.add(runtime.processStack.pop());

        JavacallContext context = new JavacallContext(thread.program.warper, self, argsList,
                isConstructor, thread, this, thread.program.holder);
        if (isLocal()) {
            handleWarper(context);
        } else {
            thread.program.requestThreads.add(context);
            thread.block();
        }
    }
}
