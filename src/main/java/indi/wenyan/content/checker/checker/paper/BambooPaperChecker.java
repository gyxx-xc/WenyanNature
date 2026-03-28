package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * PlusChecker is a simple checker that verifies if the input matches the sum of two randomly generated integers.
 * It initializes two random integers as inputs and checks if the input matches their sum.
 * <p>
 * output var0 + var1
 */
public class BambooPaperChecker extends ValueAnswerChecker {
    public BambooPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        setVariable(0, WenyanValues.of(a));
        setVariable(1, WenyanValues.of(b));
        ans = WenyanValues.of((long) a + b);
    }
}
