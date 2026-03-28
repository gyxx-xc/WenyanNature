package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * CloudPaperChecker verifies if the user correctly implemented the NOIP2015
 * knight coin problem.
 */
public class CloudPaperChecker extends ValueAnswerChecker {
    public CloudPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int k = random.nextInt(10000) + 1;
        setVariable(0, WenyanValues.of(k));

        long totalCoins = 0;
        int c = 1, q = 1;
        for (int i = 1; i <= k; i++) {
            totalCoins += c;
            q--;
            if (q == 0) {
                c++;
                q = c;
            }
        }
        ans = WenyanValues.of(totalCoins);
    }
}
