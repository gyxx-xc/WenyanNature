package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import java.math.BigInteger;

/**
 * StarlightInkChecker verifies if the user correctly implemented a factorial
 * sum loop.
 * It provides a random integer n (1 <= n <= 50) and expects the sum of 1! + 2!
 * + ... + n!
 */
public class StarlightInkChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public StarlightInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // 1 <= n <= 20
        int n = random.nextInt(20) + 1;
        setVariable(0, WenyanValues.of(n));

        Long sum = 0L;
        Long factorial = 1L;
        for (int i = 1; i <= n; i++) {
            factorial = factorial * i;
            sum = sum + factorial;
        }
        ans = WenyanValues.of(sum);
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
