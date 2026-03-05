package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents the runtime environment for executing Wenyan bytecode.
 * Stores variables, execution state, and handles the program flow.
 */
@WenyanThreading
public class WenyanRuntime {
    /**
     * -- GETTER --
     * The bytecode to be executed
     */
    @Getter @NotNull
    private final WenyanBytecode bytecode;

    /** Current instruction pointer */
    public int programCounter = 0;

    @Getter @Nullable
    private final WenyanRuntime returnRuntime;

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

    /** Flag indicating program counter was modified */
    public boolean PCFlag = false;

    @Getter @Setter
    private ReturnBehavior returnBehavior = this::onReturn;

    /**
     * Creates a new runtime environment with the specified bytecode.
     *
     * @param bytecode      The bytecode to execute (can be null)
     */
    public WenyanRuntime(@NotNull WenyanBytecode bytecode, List<IWenyanValue> refs, @Nullable WenyanRuntime returnRuntime) {
        this.bytecode = bytecode;
        this.references = refs;
        this.returnRuntime = returnRuntime;
    }

    public WenyanRuntime(@NotNull WenyanBytecode bytecode) {
        this(bytecode, Collections.emptyList(), null);
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

    public void pushReturnValue(IWenyanValue value) {
        getProcessStack().push(value);
    }

    private void onReturn(WenyanRunner runner, IWenyanValue returnValue) throws WenyanUnreachedException {
        runner.ret();
        if (returnRuntime != null)
            returnRuntime.pushReturnValue(returnValue);
    }

    @FunctionalInterface
    public interface ReturnBehavior {
        void onReturn(WenyanRunner runner, IWenyanValue returnValue) throws WenyanException;
    }
}
