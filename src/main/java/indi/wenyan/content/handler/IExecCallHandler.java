package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanDevice;

import java.util.List;
import java.util.Optional;

/**
 * Interface for handlers that execute calls in the Wenyan interpreter.
 * Manages the execution context and device.
 */
public interface IExecCallHandler extends IJavacallHandler {
    /**
     * Handles the execution call.
     *
     * @param context the context of the call
     * @return the result value
     * @throws WenyanException.WenyanThrowException if an error occurs during handling
     */
    Optional<IWenyanValue> handle(JavacallContext context) throws WenyanException.WenyanThrowException;

    /**
     * Gets the device that executes this handler.
     *
     * @return an optional containing the executor device, or empty if none is available
     */
    Optional<IWenyanDevice> getExecutor();

    /**
     * Calls the handler with the given parameters and blocks the thread.
     *
     * @param self     the self value
     * @param thread   the thread to execute on
     * @param argsList the arguments for the call
     * @throws WenyanException.WenyanThrowException if an error occurs during the call
     */
    default void call(IWenyanValue self, WenyanThread thread,
                      List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        JavacallContext context = new JavacallContext(self, argsList,
                thread, this, thread.program.holder);

        getExecutor().ifPresentOrElse((executor) -> thread.program.platform.accept(context), () -> {
            throw new WenyanException("killed by no executor");
        });
        thread.block();
    }
}
