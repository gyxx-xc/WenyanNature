package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public abstract class CraftingAnswerChecker {
    protected final ArrayList<WenyanNativeValue> input = new ArrayList<>();
    protected final ArrayList<String> inputName = new ArrayList<>();
    protected final RandomSource random;

    private Result result = Result.WRONG_ANSWER;

    public enum Result {
        ANSWER_CORRECT,
        WRONG_ANSWER,
        RUNTIME_ERROR,
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
    }

    private static final String[] DEFAULT_INPUT_NAME =
            {"「甲」", "「乙」", "「丙」", "「丁」", "「戊」", "「己」", "「庚」", "「辛」", "「壬」", "「癸」"};

    abstract protected void genInput();

    abstract public void accept(WenyanNativeValue value);

    protected void setStatus(Result result) {
        this.result = result;
    }

    public Result getResult() {
        // TODO: if tle: return tle
        return result;
    }

    public CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    public void accept(WenyanNativeValue[] value) {
        for (var v : value)
            accept(v);
    }

    public WenyanRuntime inputEnvironment() {
        genInput();
        WenyanPackageBuilder builder = WenyanPackageBuilder.create();
        for (int i = 0; i < input.size(); i++)
            builder.constant(inputName.isEmpty() ? DEFAULT_INPUT_NAME[i] : inputName.get(i), input.get(i));
        return builder.build();
    }

}
