package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanThread;

/**
 * Base class for all executable code in the Wenyan interpreter.
 */
public abstract class WenyanCode {
    /** The name of this code operation */
    public final String name;

    /**
     * Creates a new WenyanCode with the specified name.
     *
     * @param name The name of this code
     */
    protected WenyanCode(String name) {
        this.name = name;
    }

    /**
     * Executes this code with the given arguments and thread context.
     *
     * @param args The arguments for execution
     * @param thread The thread context
     */
    public abstract void exec(int args, WenyanThread thread);

    /**
     * Gets the number of steps this code execution requires.
     *
     * @param args The arguments for execution
     * @param thread The thread context
     * @return The number of steps
     */
    public int getStep(int args, WenyanThread thread) {
        return 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
