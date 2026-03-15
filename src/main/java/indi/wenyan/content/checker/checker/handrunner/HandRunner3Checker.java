package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import static java.lang.Math.sqrt;

public class HandRunner3Checker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public HandRunner3Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int n = random.nextInt(1000000) + 1;
        setVariable(0,WenyanValues.of(n));

        if (n <= 1) {
            ans = WenyanValues.of(false);
            return;
        }

        for (int i=2; i<=(int)sqrt(n); i++)
        {
            if (n%i == 0){
                ans = WenyanValues.of(false);
                return;
            }
        }
        ans = WenyanValues.of(true);
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
