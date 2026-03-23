package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class FrostPaperChecker extends CraftingAnswerChecker {
    public FrostPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int target = random.nextInt(100) + 1;
        long[] a = new long[target + 1];
        a[0] = 0;

        for (int i = 1; i <= target; i++) {
            a[i] = a[i - 1] * 2 + 2;
        }
        ans = WenyanValues.of(a[target]);
    }
}
