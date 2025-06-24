package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

public interface IAnsweringChecker {
    void init(WenyanProgram program);

    void accept(IWenyanValue value) throws WenyanException.WenyanCheckerError;

    IAnsweringChecker.Result getResult();

    enum Result {
        ANSWER_CORRECT,
        WRONG_ANSWER,
        RUNTIME_ERROR,
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
    }
}
