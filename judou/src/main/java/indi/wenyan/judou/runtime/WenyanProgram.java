package indi.wenyan.judou.runtime;

import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanThrowException;

public interface WenyanProgram {
    /**
     * Creates a new thread for this program with the base environment.
     */
    WenyanThread createThread(String code) throws WenyanThrowException;

    /**
     * Allocates execution steps to the program.
     *
     * @param steps Number of execution steps to allocate
     */
    void step(int steps);

    /**
     * Stops the program by interrupting the scheduler thread.
     */
    void stop();

    /**
     * Checks if the program has any running threads.
     *
     * @return True if the program has running threads, false otherwise
     */
    boolean isRunning();
}
