package indi.wenyan.content.checker.checker.handrunner;

import com.google.common.math.IntMath;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class HandRunner6Checker extends CraftingAnswerChecker {
    public HandRunner6Checker(RandomSource random) {
        super(random);
    }

    private int a, m;
    // a * x === 1 (mod m)

    @Override
    public void init() {
        super.init();
        a = random.nextInt(2, Integer.MAX_VALUE);
        m = random.nextInt(2, Integer.MAX_VALUE);

        setVariable(0, WenyanValues.of(a));
        setVariable(1, WenyanValues.of(m));
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException {
        int i = value.as(WenyanInteger.TYPE).value();
        if (IntMath.gcd(a, m) != 1) {
            setResult(i == -1 ? ResultStatus.ANSWER_CORRECT : ResultStatus.WRONG_ANSWER);
            return;
        }
        if (((long) i * a) % m == 1) {
            setResult(ResultStatus.ANSWER_CORRECT);
        } else {
            setResult(ResultStatus.WRONG_ANSWER);
        }
    }
}
