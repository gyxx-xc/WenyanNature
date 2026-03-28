package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;

public class PhoenixPaperChecker extends ValueAnswerChecker {
    public PhoenixPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
    }
}
