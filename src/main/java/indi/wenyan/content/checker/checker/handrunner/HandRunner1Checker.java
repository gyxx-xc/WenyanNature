package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * HandRunner1Checker verifies if the user correctly implemented a conditional.
 * It provides a random integer and expects true if it is greater than 5, false
 * otherwise.
 */
public class HandRunner1Checker extends ValueAnswerChecker {
    public HandRunner1Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int a = random.nextInt(10000);
        int b = random.nextInt(10000);
        setVariable(0, WenyanValues.of(a));
        setVariable(1, WenyanValues.of(b));
        ans = WenyanValues.of(a > b);
    }

}
