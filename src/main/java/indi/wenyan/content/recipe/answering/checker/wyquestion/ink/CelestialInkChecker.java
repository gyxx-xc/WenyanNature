package indi.wenyan.content.recipe.answering.checker.wyquestion.ink;

import indi.wenyan.content.recipe.answering.checker.ValueAnswerChecker;
import indi.wenyan.judou.api.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CelestialInkChecker extends ValueAnswerChecker {
    public CelestialInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();

        // 1. 随机生成上限 (1 到 5,000,000)
        int target = random.nextInt(5000000) + 1;
        setVariable(0, WenyanValues.of(target));

        // 2. 线性筛 (欧拉筛) 计算质数总数
        long totalPrimes = 0;
        if (target >= 2) {
            boolean[] isPrime = new boolean[target + 1];
            Arrays.fill(isPrime, true);
            isPrime[0] = isPrime[1] = false;
            List<Integer> primes = new ArrayList<>();

            for (int i = 2; i <= target; i++) {
                if (isPrime[i]) {
                    primes.add(i);
                }
                // 核心筛法逻辑
                for (int p : primes) {
                    if (i * p > target) break;
                    isPrime[i * p] = false;
                    if (i % p == 0) break; // 保证每个合数只被最小质因子筛一次
                }
            }
            totalPrimes = primes.size();
        }

        // 5. 写入最终答案
        ans = WenyanValues.of(totalPrimes);
    }
}
