package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class CelestialInkChecker extends CraftingAnswerChecker {
    public CelestialInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
    }
}
