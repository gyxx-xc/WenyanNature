package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.deprecated_JavacallRequest;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanThreading;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    boolean handle(@NotNull IHandleContext context, @NotNull deprecated_JavacallRequest request) throws WenyanException.WenyanThrowException;

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
        deprecated_JavacallRequest request = new deprecated_JavacallRequest(self, argsList,
                thread, this);
//        thread.program.platform.receive(request);
        thread.block();
    }
}
