package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * CloudPaperChecker verifies if the user correctly implemented the NOIP2015
 * knight coin problem.
 */
public class CloudPaperChecker extends CraftingAnswerChecker {
    public CloudPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int k = random.nextInt(10000) + 1;
        setVariable(0, WenyanValues.of(k));

        long totalCoins = 0;
        int currentDay = 1;
        int coinsPerDay = 1;

        while (currentDay <= k) {
            for (int i = 0; i < coinsPerDay && currentDay <= k; i++) {
                totalCoins += coinsPerDay;
                currentDay++;
            }
            coinsPerDay++;
        }

        ans = WenyanValues.of(totalCoins);
    }
}
