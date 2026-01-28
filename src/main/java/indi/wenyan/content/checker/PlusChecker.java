package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * PlusChecker is a simple checker that verifies if the input matches the sum of two randomly generated integers.
 * It initializes two random integers as inputs and checks if the input matches their sum.
 * <p>
 * output var0 + var1
 */
public class PlusChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public PlusChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        setVariable(0, WenyanValues.of(a));
        setVariable(1, WenyanValues.of(b));
        ans = WenyanValues.of(a + b);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanThrowException {
        try {
            if (IWenyanValue.equals(value, ans)) {
                setResult(ResultStatus.ANSWER_CORRECT);
            } else {
                setResult(ResultStatus.WRONG_ANSWER);
            }
        } catch (Exception e) {
            setResult(ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
    }
}
