package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class CyberHackerCheckerTest {

    // 提供正确的随机序列与期望的布尔值
    static Stream<Arguments> correctAnswersProvider() {
        return Stream.of(
                // 测试用例 1：可以成功下载 (返回 true)
                // 注入随机数：35(即 M=36), 9(即 T=10), 254(即 S=255)
                // 解释：初始容量 36，10秒内靠爆发(50MB/次)和清理缓存(+4容量)，配合常规下载(15MB/次)。
                // 最终在第 9 秒时可以达到 260 MB 的下载量，大于目标的 255 MB。
                Arguments.of(new int[]{35, 9, 254}, true),

                // 测试用例 2：无法完成下载 (返回 false)
                // 注入随机数：38(即 M=39), 3(即 T=4), 199(即 S=200)
                // 解释：初始容量 39，4秒内最快只能下载 165 MB (前三次爆发 150，第四次转为常规下载 +15)，达不到 200 MB。
                Arguments.of(new int[]{38, 3, 199}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("correctAnswersProvider")
    void testCorrectAnswer(int[] randomSeq, boolean expected) throws WenyanException {
        MockRandomSource.InputBuilder builder = MockRandomSource.InputBuilder.create();
        for (int num : randomSeq) {
            builder.addSeq(num);
        }
        RandomSource random = builder.build();

        HandRunner4Checker checker = new HandRunner4Checker(random);
        checker.init();

        // 提交期望的正确答案
        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // 验证查错逻辑（给相反的答案测试是否判错）
    static Stream<Arguments> wrongAnswersProvider() {
        return Stream.of(
                // 本该成功(true)，玩家答失败(false)
                Arguments.of(new int[]{35, 9, 254}, false),
                // 本该失败(false)，玩家答成功(true)
                Arguments.of(new int[]{38, 3, 199}, true)
        );
    }

    @ParameterizedTest
    @MethodSource("wrongAnswersProvider")
    void testWrongAnswer(int[] randomSeq, boolean wrongExpected) throws WenyanException {
        MockRandomSource.InputBuilder builder = MockRandomSource.InputBuilder.create();
        for (int num : randomSeq) {
            builder.addSeq(num);
        }
        RandomSource random = builder.build();

        HandRunner4Checker checker = new HandRunner4Checker(random);
        checker.init();

        // 提交错误的答案
        checker.accept(WenyanValues.of(wrongExpected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}