package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.util.RandomSource;

public abstract class CraftingAnswerChecker implements IAnsweringChecker {
    protected final RandomSource random;
    protected WenyanProgram program;

    private Result result = Result.WRONG_ANSWER;

    private static final String[] DEFAULT_INPUT_NAME =
            {"「甲」", "「乙」", "「丙」", "「丁」", "「戊」", "「己」", "「庚」", "「辛」", "「壬」", "「癸」"};

    protected void setStatus(Result result) {
        this.result = result;
    }

    @Override
    public Result getResult() {
        // TODO: if tle: return tle
        return result;
    }

    protected CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    public void accept(Iterable<IWenyanValue> value) throws WenyanException.WenyanCheckerError {
        for (var v : value)
            accept(v);
    }

    @Override
    public void init(WenyanProgram program) {
        this.program = program;
    }

    protected void setVariable(int i, IWenyanValue value) {
        if (program == null)
            throw new IllegalStateException("Program is not initialized");
        program.baseEnvironment.setVariable(DEFAULT_INPUT_NAME[i], value);
    }

    protected void setAttribute(String name, IWenyanValue value) {
        if (program == null)
            throw new IllegalStateException("Program is not initialized");
        program.baseEnvironment.setVariable(name, value);
    }
}
