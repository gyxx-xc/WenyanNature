package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * HandRunner1Checker verifies if the user correctly implemented a conditional.
 * It provides a random integer and expects true if it is greater than 5, false
 * otherwise.
 */
public class HandRunner1Checker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public HandRunner1Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int a = random.nextInt(10000);
        int b = random.nextInt(10000);
        setVariable(0, WenyanValues.of(a));
        setVariable(1, WenyanValues.of(b));
        ans = WenyanValues.of(a > b);
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
