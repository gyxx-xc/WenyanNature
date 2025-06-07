package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.CraftingAnswerChecker;
import net.minecraft.util.RandomSource;

public class PlusChecker extends CraftingAnswerChecker {
    private WenyanValue ans;

    public PlusChecker(RandomSource random) {
        super(random);
    }

    @Override
    protected void genInput() {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        input.clear();
        input.add(new WenyanValue(WenyanValue.Type.INT, a, true));
        input.add(new WenyanValue(WenyanValue.Type.INT, b, true));
        ans = new WenyanValue(WenyanValue.Type.INT, a + b, true);
    }

    @Override
    public void accept(WenyanValue value) {
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
