package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

public interface AnsweringChecker {
    void init(WenyanProgram program);

    void accept(WenyanNativeValue value);

    CraftingAnswerChecker.Result getResult();

    enum Result {
        ANSWER_CORRECT,
        WRONG_ANSWER,
        RUNTIME_ERROR,
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
    }
}
