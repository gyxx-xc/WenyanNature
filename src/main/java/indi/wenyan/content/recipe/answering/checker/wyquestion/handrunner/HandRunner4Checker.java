package indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner;

import indi.wenyan.content.recipe.answering.checker.ValueAnswerChecker;
import indi.wenyan.judou.api.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class HandRunner4Checker extends ValueAnswerChecker {

    public HandRunner4Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();

        // 1. 随机生成参数
        int m = random.nextInt(100) + 1;  // 初始缓冲区容量 M
        int t = random.nextInt(1000) + 1; // 剩余时间 T
        int s = random.nextInt(10000) + 1; // 目标数据量 S

        // 记录题目变量供展示
        setVariable(0, WenyanValues.of(m));
        setVariable(1, WenyanValues.of(t));
        setVariable(2, WenyanValues.of(s));

        int[] dp = new int[t + 1];
        int currentM = m;

        // 第一阶段：贪心策略。只考虑“极速爆发”和“清理缓存”
        for (int i = 1; i <= t; i++) {
            if (currentM >= 10) {
                dp[i] = dp[i - 1] + 50;
                currentM -= 10;
            } else {
                dp[i] = dp[i - 1]; // 不下载数据
                currentM += 4;
            }
        }

        // 第二阶段：动态规划。将“常规下载”加入决策，取两者最优解
        for (int i = 1; i <= t; i++) {
            dp[i] = Math.max(dp[i], dp[i - 1] + 15);

            // 如果在 t 秒内的某一秒达到了目标数据量，则成功
            if (dp[i] >= s) {
                ans = WenyanValues.of(true);
                return;
            }
        }

        // 耗尽时间仍未达到目标
        ans = WenyanValues.of(false);
    }
}