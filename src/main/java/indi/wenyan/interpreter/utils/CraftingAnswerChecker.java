package indi.wenyan.interpreter.utils;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public abstract class CraftingAnswerChecker {
    protected final ArrayList<WenyanValue> ans = new ArrayList<>();
    protected final ArrayList<WenyanValue> input = new ArrayList<>();
    protected final ArrayList<String> inputName = new ArrayList<>();
    protected final RandomSource random;

    private static final String[] DEFAULT_INPUT_NAME =
            {"「甲」", "「乙」", "「丙」", "「丁」", "「戊」", "「己」", "「庚」", "「辛」", "「壬」", "「癸」"};

    public CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    public void add(WenyanValue value) {
        ans.add(value);
    }

    public void add(WenyanValue[] value) {
        ans.addAll(List.of(value));
    }

    public WenyanFunctionEnvironment inputEnvironment() {
        genInput();
        WenyanPackageBuilder builder = WenyanPackageBuilder.create();
        for (int i = 0; i < input.size(); i++)
            builder.constant(inputName.isEmpty() ? DEFAULT_INPUT_NAME[i] : inputName.get(i), input.get(i));
        return builder.build();
    }

    abstract protected void genInput();

    abstract public boolean check();
}
