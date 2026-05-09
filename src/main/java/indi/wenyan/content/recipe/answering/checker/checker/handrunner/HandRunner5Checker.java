package indi.wenyan.content.recipe.answering.checker.checker.handrunner;

import indi.wenyan.content.recipe.answering.checker.ValueAnswerChecker;
import indi.wenyan.judou.api.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class HandRunner5Checker extends ValueAnswerChecker {
    public HandRunner5Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
    }
}
