package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class DragonPaperChecker extends ValueAnswerChecker {
    public DragonPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        // TODO: Implement specific initialization logic and answer generation
        ans = WenyanValues.of(0);
    }
}
