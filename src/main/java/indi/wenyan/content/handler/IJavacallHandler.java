package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;

public interface IJavacallHandler extends IWenyanFunction {
    WenyanType<IJavacallHandler> TYPE = new WenyanType<>("javacall_handler", IJavacallHandler.class);

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

    default WenyanType<?> type() {
        return TYPE;
    }
}
