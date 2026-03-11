package indi.wenyan.content.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class CelestialInkChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public CelestialInkChecker(RandomSource random) {
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
