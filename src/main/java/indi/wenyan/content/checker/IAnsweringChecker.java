package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

/**
 * Interface for checkers that validate answers in Wenyan programs.
 * Handles initialization, value acceptance, and result reporting.
 */
public interface IAnsweringChecker {
    /**
     * Initializes the checker with a Wenyan program.
     */
    void init();

    /**
     * Processes a value for checking.
     *
     * @param value the value to check
     * @throws WenyanException.WenyanCheckerError if a checking error occurs
     */
    void accept(IWenyanValue value) throws WenyanThrowException;

    default void accept(Iterable<IWenyanValue> value) throws WenyanThrowException {
        for (var v : value)
            accept(v);
    }

    IWenyanObject getArgs();

    /**
     * Gets the current result status of the checker.
     *
     * @return the result status
     */
    ResultStatus getResult();

    /**
     * Possible result states for an answer checker.
     */
    enum ResultStatus {
        RUNNING,
        ANSWER_CORRECT,
        WRONG_ANSWER,
//        RUNTIME_ERROR,
//        COMPILE_ERROR,
//        TIME_LIMIT_EXCEEDED,
    }
}
