package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * StarlightInkChecker verifies if the user correctly implemented a factorial
 * sum loop.
 * It provides a random integer n (1 <= n <= 50) and expects the sum of 1! + 2!
 * + ... + n!
 */
public class StarlightInkChecker extends ValueAnswerChecker {
    public StarlightInkChecker(RandomSource random) {
        super(random);
    }
    @Override
    public void init() {
        super.init();
        // 1 <= n <= 20
        int n = random.nextInt(20) + 1;
        setVariable(0, WenyanValues.of(n));

        long sum = 0L;
        long factorial = 1L;
        for (int i = 1; i <= n; i++) {
            factorial = factorial * i;
            sum = sum + factorial;
        }
        ans = WenyanValues.of(sum);
    }
}
