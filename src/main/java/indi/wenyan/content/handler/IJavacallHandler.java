package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public interface IJavacallHandler extends IWenyanFunction {
    WenyanType<IJavacallHandler> TYPE = new WenyanType<>("javacall_handler", IJavacallHandler.class);
    IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException;

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

    default Optional<IWenyanExecutor> getExecutor() {
        return Optional.empty();
    }

    @Override
    default void call(IWenyanValue self, WenyanThread thread,
                      List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException{
        JavacallContext context = new JavacallContext(thread.program.warper, self, argsList,
                thread, this, thread.program.holder);

        if (getExecutor().isPresent()){
            getExecutor().get().exec(context);
            thread.block();
            return;
        }
        if (isLocal(context)) {
            context.thread().currentRuntime().processStack.push(handle(context));
        } else {
            thread.program.requestThreads.add(context);
            thread.block();
        }
    }

    default WenyanType<?> type() {
        return TYPE;
    }
}
