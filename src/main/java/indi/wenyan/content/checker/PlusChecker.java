package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.util.RandomSource;

public class PlusChecker extends CraftingAnswerChecker {
    private WenyanNativeValue ans;

    public PlusChecker(RandomSource random) {
        super(random);
    }

    @Override
    protected void genInput() {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        input.clear();
        input.add(new WenyanNativeValue(WenyanNativeValue.Type.INT, a, true));
        input.add(new WenyanNativeValue(WenyanNativeValue.Type.INT, b, true));
        ans = new WenyanNativeValue(WenyanNativeValue.Type.INT, a + b, true);
    }

    @Override
    public void accept(WenyanNativeValue value) {
        try {
            if (value.equals(ans)) {
                setStatus(Result.ANSWER_CORRECT);
            } else {
                setStatus(Result.WRONG_ANSWER);
            }
        } catch (Exception e) {
            setStatus(Result.RUNTIME_ERROR);
        }
    }
}
