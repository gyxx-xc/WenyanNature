package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class HandRunner6Checker extends ValueAnswerChecker {
    public HandRunner6Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
    }
}
