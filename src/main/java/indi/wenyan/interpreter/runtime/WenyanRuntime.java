package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;

import java.util.HashMap;
import java.util.Stack;

/**
 * Represents the runtime environment for executing Wenyan bytecode.
 * Stores variables, execution state, and handles the program flow.
 */
public class WenyanRuntime {
    /** The bytecode to be executed */
    public final WenyanBytecode bytecode;

    /** Storage for program variables */
    public final HashMap<String, IWenyanValue> variables = new HashMap<>();

    /** Stack for operation results */
    public final WenyanStack resultStack = new WenyanStack();

    /** Stack for processing intermediate values */
    public final Stack<IWenyanValue> processStack = new Stack<>();

    /** Current instruction pointer */
    public int programCounter = 0;

    /** Flag indicating program counter was modified */
    public boolean PCFlag = false;

    /** Flag indicating no return value expected */
    public boolean noReturnFlag = false;

    /**
     * Creates a new runtime environment with the specified bytecode.
     *
     * @param bytecode The bytecode to execute (can be null)
     */
    public WenyanRuntime(WenyanBytecode bytecode) {
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
     * @param environment The package containing variables to import
     */
    public void importEnvironment(WenyanPackage environment) {
        variables.putAll(environment.getVariables());
    }
}
