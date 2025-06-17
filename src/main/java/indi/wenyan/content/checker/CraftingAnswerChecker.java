package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public abstract class CraftingAnswerChecker implements AnsweringChecker {
    protected final ArrayList<WenyanNativeValue> input = new ArrayList<>();
    protected final ArrayList<String> inputName = new ArrayList<>();
    protected final RandomSource random;

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

    public CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    public void accept(List<WenyanNativeValue> value) {
        for (var v : value)
            accept(v);
    }

    public WenyanRuntime inputEnvironment() {
        init();
        WenyanPackageBuilder builder = WenyanPackageBuilder.create();
        for (int i = 0; i < input.size(); i++)
            builder.constant(inputName.isEmpty() ? DEFAULT_INPUT_NAME[i] : inputName.get(i), input.get(i));
        return builder.build();
    }

}
