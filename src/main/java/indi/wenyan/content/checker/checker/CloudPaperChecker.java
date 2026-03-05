package indi.wenyan.content.checker.checker;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * CloudPaperChecker verifies if the user correctly implemented the NOIP2015
 * knight coin problem.
 */
public class CloudPaperChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public CloudPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int k = random.nextInt(10000) + 1;
        setVariable(0, WenyanValues.of(k));

        long totalCoins = 0;
        int c = 1, q = 1;
        for (int i = 1; i <= k; i++) {
            totalCoins += c;
            q--;
            if (q == 0) {
                c++;
                q = c;
            }
        }
        ans = WenyanValues.of(totalCoins);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException {
        try {
            if (IWenyanValue.equals(value, ans)) {
                setResult(IAnsweringChecker.ResultStatus.ANSWER_CORRECT);
            } else {
                setResult(IAnsweringChecker.ResultStatus.WRONG_ANSWER);
            }
        } catch (WenyanException e) {
            setResult(IAnsweringChecker.ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
    }
}
