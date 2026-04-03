package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CelestialInkChecker extends ValueAnswerChecker {
    public CelestialInkChecker(RandomSource random) {
        super(random);
    }
    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    @Override
    public void init() {
        super.init();

        // 1. 随机生成上限 (1 到 5,000,000)
        int target = random.nextInt(5000000) + 1;

        // 记录输入变量以供题目文本读取
        setVariable(0, WenyanValues.of(target));

        // 2. 初始化线程池 (根据系统核心数分配)
        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = new ArrayList<>();

        int chunkSize = target / threadCount;

        // 3. 分配多线程任务
        for (int i = 0; i < threadCount; i++) {
            final int startIdx = i * chunkSize + 1;
            // 最后一个线程处理剩余的所有数字
            final int endIdx = (i == threadCount - 1) ? target : (i + 1) * chunkSize;

            futures.add(executor.submit(() -> {
                int count = 0;
                for (int j = startIdx; j <= endIdx; j++) {
                    if (isPrime(j)) count++;
                }
                return count;
            }));
        }

        // 4. 汇总结果
        long totalPrimes = 0;
        try {
            for (Future<Integer> future : futures) {
                totalPrimes += future.get(); // 阻塞等待并累加每个线程的结果
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("多线程计算出现异常", e);
        } finally {
            // 确保线程池关闭，防止内存泄漏
            executor.shutdown();
        }
        // 5. 写入最终答案
        ans = WenyanValues.of(totalPrimes);
    }
}
