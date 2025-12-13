package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IExecReceiver;
import indi.wenyan.interpreter.utils.IHandleContext;
import indi.wenyan.interpreter.utils.WenyanThreading;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Interface for handlers that execute calls in the Wenyan interpreter.
 * Manages the execution context and device.
 */
@MethodsReturnNonnullByDefault
public interface IExecCallHandler extends IJavacallHandler {
    /**
     * Handles the execution call.
     * if not finish return empty
     *
     * @param context the execution context
     * @param request the request of the call
     * @return the result value
     * @throws WenyanException.WenyanThrowException if an error occurs during handling
     */
    boolean handle(@NotNull IHandleContext context, @NotNull JavacallRequest request) throws WenyanException.WenyanThrowException;

    /**
     * Gets the device that executes this handler.
     * optional happened when device is destroyed (chunk unload / dig) when running
     *
     * @return an optional containing the executor device, or empty if none is available
     */
    @WenyanThreading
    Optional<IExecReceiver> getExecutor();

    /**
     * Calls the handler with the given parameters and blocks the thread.
     *
     * @param self     the self value
     * @param thread   the thread to execute on
     * @param argsList the arguments for the call
     */
    @Override
    @WenyanThreading
    default void call(IWenyanValue self, WenyanThread thread,
                      List<IWenyanValue> argsList) {
        if (getExecutor().isEmpty())
            throw new WenyanException("killed by no executor");

        JavacallRequest request = new JavacallRequest(self, argsList,
                thread, this);
        getExecutor().ifPresent(executor -> executor.receive(request));
        thread.program.platform.notice(request);
        thread.block();
    }
}
