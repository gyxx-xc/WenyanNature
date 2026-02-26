package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * CinnabarInkChecker verifies if the user correctly implemented a halving loop.
 * It provides a random integer and expects the number of days until the length
 * reaches 1
 * when it is halved daily.
 */
public class CinnabarInkChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public CinnabarInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // 1 <= a <= 10^9
        int a = random.nextInt(1_000_000_000) + 1;
        setVariable(0, WenyanValues.of(a));

        int day = 1;
        int length = a;
        while (length > 1) {
            length /= 2;
            day++;
        }
        ans = WenyanValues.of(day);
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
