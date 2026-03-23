package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class HandRunner4Checker extends CraftingAnswerChecker {
    public HandRunner4Checker(RandomSource random) {
        super(random);
    }
    @Override
    public void init() {
        super.init();
        int m =  random.nextInt(100) + 1;
        int t =  random.nextInt(1000) + 1;
        int s =  random.nextInt(10000) + 1;

        int[] dp = new int[300005];
        int z;

        for (int i = 1; i <= t; i++) {
            dp[i] = dp[i - 1] + (m >= 10 ? 60 : 0);
            z = m;
            m -= (z >= 10 ? 10 : 0);
            m += (z < 10 ? 4 : 0);
        }

        for (int i = 1; i <= t; i++) {
            dp[i] = Math.max(dp[i], dp[i - 1] + 17);
            if (dp[i] >= s) {
                ans = WenyanValues.of(true);
                return;
            }
        }
        ans = WenyanValues.of(false);
    }
}
