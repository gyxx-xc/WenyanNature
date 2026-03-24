package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import net.minecraft.util.RandomSource;

public abstract class ValueAnswerChecker extends CraftingAnswerChecker {
    protected IWenyanValue ans;

    protected ValueAnswerChecker(RandomSource random) {
        super(random);
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
