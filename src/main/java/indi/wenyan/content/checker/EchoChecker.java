package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * EchoChecker is a simple checker that verifies if the input matches a randomly generated integer.
 * It initializes a random integer as the answer and checks if the input matches this value.
 * <p>
 * output var0
 */
public class EchoChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        ans = WenyanValues.of(random.nextInt());
        setVariable(0, ans);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanThrowException {
        try {
            if (IWenyanValue.equals(value, ans)){
                setResult(ResultStatus.ANSWER_CORRECT);
                return;
            }
        } catch (WenyanThrowException e) {
            setResult(ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
        setResult(ResultStatus.WRONG_ANSWER);
    }
}
