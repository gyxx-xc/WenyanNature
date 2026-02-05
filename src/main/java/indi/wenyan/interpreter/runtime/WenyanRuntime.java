package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanThreading;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the runtime environment for executing Wenyan bytecode.
 * Stores variables, execution state, and handles the program flow.
 */
@WenyanThreading
public class WenyanRuntime {
    /** The bytecode to be executed */
    @Nullable public final WenyanBytecode bytecode;

    /** Storage for program variables */
    // TODO: thread safety check
    public final Map<String, IWenyanValue> variables = new HashMap<>();

    /** Stack for operation results */
    public final WenyanResultStack resultStack = new WenyanResultStack();

    /** Stack for processing intermediate values */
    public final Deque<IWenyanValue> processStack = new ArrayDeque<>();

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
    public WenyanRuntime(@Nullable WenyanBytecode bytecode) {
        this.bytecode = bytecode;
    }

    /**
     * Sets a variable in the current runtime scope.
     *
     * @param id The variable identifier
     * @param value The value to store
     */
    public void setVariable(String id, IWenyanValue value) {
        variables.put(id, value);
    }

    /**
     * Imports all variables from a package into this runtime.
     *
     * @param aPackage The package containing variables to import
     */
    public void importPackage(WenyanPackage aPackage) {
        variables.putAll(aPackage.variables());
    }
}
