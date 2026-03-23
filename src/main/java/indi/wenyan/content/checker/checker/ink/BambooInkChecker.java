package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * PlusChecker is a simple checker that verifies if the input matches the sum of
 * two randomly generated integers.
 * It initializes two random integers as inputs and checks if the input matches
 * their sum.
 * <p>
 * output var0 + var1
 */
public class BambooInkChecker extends CraftingAnswerChecker {
    public BambooInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        ans = WenyanValues.of("吾有一術");
    }
}
