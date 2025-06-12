package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
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
        input.add(new WenyanNativeValue(WenyanType.INT, a, true));
        input.add(new WenyanNativeValue(WenyanType.INT, b, true));
        ans = new WenyanNativeValue(WenyanType.INT, a + b, true);
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
