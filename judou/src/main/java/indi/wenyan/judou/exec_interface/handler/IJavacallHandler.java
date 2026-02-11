package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.runtime.WenyanThread;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;

/**
 * Interface for handlers that bridge between Java and Wenyan code.
 * Provides type information and step calculation.
 */
public interface IJavacallHandler extends IWenyanFunction {
    /** Type identifier for Javacall handlers */
    WenyanType<IJavacallHandler> TYPE = new WenyanType<>("javacall_handler", IJavacallHandler.class);

    /**
     * The step of this handler.
     * <p>
     * It can be decided by the feature of the handler for game balance,
     * e.g. powerful handler may take more time to execute.
     * However, it's better to keep the handler time not longer than O(step),
     * which may cause the program to be stuck.
     *
     * @param args number of arguments
     * @param thread the thread executing this handler
     * @return the step of this handler
     */
    @SuppressWarnings({"unused", "SameReturnValue"})
    default int getStep(int args, WenyanThread thread) {
        return 1;
    }

    default WenyanType<?> type() {
        return TYPE;
    }
}
