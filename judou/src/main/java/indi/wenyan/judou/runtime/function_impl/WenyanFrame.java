package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

/**
 * Represents the runtime environment for executing Wenyan bytecode.
 * Stores variables, execution state, and handles the program flow.
 */
// TODO: to interface
public class WenyanFrame {
    /**
     * -- GETTER --
     * The bytecode to be executed
     */
    @Getter
    @NotNull
    private final IWenyanBytecode bytecode;

    /**
     * -- GETTER --
     * Current instruction pointer
     */
    @Setter
    @Getter
    private int programCounter = 0;

    @Getter
    @Nullable
    private final WenyanFrame returnRuntime;

    @Getter
    private final List<IWenyanValue> locals = new ArrayList<>();

    @Getter
    private final List<IWenyanValue> references;

    /**
     * -- GETTER --
     * Stack for operation results
     */
    @Getter
    private final WenyanResultStack resultStack = new WenyanResultStack();

    /**
     * -- GETTER --
     * Stack for processing intermediate values
     */
    @Getter
    private final Deque<IWenyanValue> processStack = new ArrayDeque<>();

    @Getter
    @Setter
    private boolean PCFlag = false;

    @Getter
    final private ReturnBehavior returnBehavior;

    /**
     * Creates a new runtime environment with the specified bytecode.
     *
     * @param bytecode The bytecode to execute (can be null)
     */
    public WenyanFrame(@NotNull IWenyanBytecode bytecode, List<IWenyanValue> refs, @Nullable WenyanFrame returnRuntime, ReturnBehavior onReturn) {
        this.bytecode = bytecode;
        this.references = refs;
        this.returnRuntime = returnRuntime;
        this.returnBehavior = onReturn;
    }

    public static @NotNull WenyanFrame ofCode(IWenyanBytecode code) {
        return new WenyanFrame(code, Collections.emptyList(), null,
                (runner, _) -> runner.getFrameManager().ret());
    }

    public void setLocal(int index, IWenyanValue value) {
        int currentSize = locals.size();
        assert index >= 0;
        if (index < currentSize) {
            locals.set(index, value);
        } else if (index == currentSize) {
            locals.add(value);
        } else {
            // however, this is not being entered normally
            for (int i = currentSize; i < index; i++) locals.add(null);
            locals.add(value);
        }
    }

    public WenyanException.@Nullable ErrorContext getErrorContext(WenyanException e, Logger logger) {
        WenyanException.ErrorContext errorContext = null;
        try {
            IWenyanBytecode.Context context = bytecode.getContext(getProgramCounter() - 1);
            if (context != null)
                errorContext = new WenyanException.ErrorContext(
                        context.line(), context.column(),
                        bytecode.getSourceCode().substring(context.contentStart(), context.contentEnd()));
        } catch (IndexOutOfBoundsException ignore) {// cause error context be null, handled below
        }
        if (errorContext == null)
            logger.error("Unexpected, failed to get code context during handling an exception", e);
        return errorContext;
    }

    @FunctionalInterface
    public interface ReturnBehavior {
        void onReturn(IWenyanRunner runner, IWenyanValue returnValue) throws WenyanException;
    }
}
