package indi.wenyan.content.checker.paper;

import indi.wenyan.content.checker.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import java.math.BigInteger;

public class StarlightPaperChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public StarlightPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();

        int n = random.nextInt(19) + 1; // 1 <= n <= 20
        setVariable(0, WenyanValues.of(n));

        long sum = 0;
        long currentFactorial = 1;

        for (int i = 1; i <= n; i++) {
            currentFactorial = currentFactorial*i;
            sum += currentFactorial;
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
