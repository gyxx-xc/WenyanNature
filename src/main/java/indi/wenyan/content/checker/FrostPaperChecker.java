package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class FrostPaperChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public FrostPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
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
