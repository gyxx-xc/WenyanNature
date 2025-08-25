package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

/**
 * Interface for checkers that validate answers in Wenyan programs.
 * Handles initialization, value acceptance, and result reporting.
 */
public interface IAnsweringChecker {
    /**
     * Initializes the checker with a Wenyan program.
     *
     * @param program the program to check
     */
    void init(WenyanProgram program);

    /**
     * Processes a value for checking.
     *
     * @param value the value to check
     * @throws WenyanException.WenyanCheckerError if a checking error occurs
     */
    void accept(IWenyanValue value) throws WenyanException.WenyanCheckerError;

    /**
     * Gets the current result status of the checker.
     *
     * @return the result status
     */
    IAnsweringChecker.Result getResult();

    /**
     * Possible result states for an answer checker.
     */
    enum Result {
        ANSWER_CORRECT,
        WRONG_ANSWER,
        RUNTIME_ERROR,
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
    }
}
