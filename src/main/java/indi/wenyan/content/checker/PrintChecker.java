package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * PlusChecker is a simple checker that verifies if the input matches the sum of
 * two randomly generated integers.
 * It initializes two random integers as inputs and checks if the input matches
 * their sum.
 * <p>
 * output var0 + var1
 */
public class PrintChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public PrintChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        ans = WenyanValues.of("吾有一術");
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException {
        try {
            if (IWenyanValue.equals(value, ans)) {
                setResult(ResultStatus.ANSWER_CORRECT);
            } else {
                setResult(ResultStatus.WRONG_ANSWER);
            }
        } catch (WenyanException e) {
            setResult(ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
    }
}
