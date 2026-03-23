package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class LunarInkChecker extends CraftingAnswerChecker {
    public LunarInkChecker(RandomSource random) {
        super(random);
    }
    @Override
    public void init() {
        super.init();
        int target = random.nextInt(1000000) + 1;
        setVariable(0, WenyanValues.of(target));

        int counter = 0;
        while (target > 1) {
            if (target % 2 == 0)
                target = target / 2;
            else
                target = target - 1;
            counter++;
        }
        ans = WenyanValues.of(counter);
    }
}
