package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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

    /** Current instruction pointer */
    public int programCounter = 0;

    /** Flag indicating program counter was modified */
    public boolean PCFlag = false;

    /** Flag indicating no return value expected */
    public boolean noReturnFlag = false;

    public boolean finishFlag = false;

    /**
     * Creates a new runtime environment with the specified bytecode.
     *
     * @param bytecode The bytecode to execute (can be null)
     */
    public WenyanRuntime(@NotNull WenyanBytecode bytecode, List<IWenyanValue> refs) {
        this.bytecode = bytecode;
        this.references = refs;
    }

    public void pushReturnValue(IWenyanValue value) {
        getProcessStack().push(value);
    }
}
