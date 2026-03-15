package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;


public class StartlightPaperChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public StartlightPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        WenyanList rows = new WenyanList();
        int row = random.nextInt(20) + 1;
        int col = random.nextInt(20) + 1;
        int maxRow = -1;
        int maxCol = -1;
        int maxVal = 0;
        for (int i = 0; i < row; i++) {
            WenyanList cols = new WenyanList();
            for (int j = 0; j < col; j++) {
                int val = random.nextInt(99) + 1;
                cols.add(WenyanValues.of(val));
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = i;
                    maxCol = j;
                }
            }
            rows.add(cols);
        }
        WenyanList ansList = new WenyanList();
        ansList.add(WenyanValues.of(maxRow));
        ansList.add(WenyanValues.of(maxCol));
        ans = ansList;
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
