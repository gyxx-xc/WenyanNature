package indi.wenyan.content.recipe.answering.checker.checker.ink;

import indi.wenyan.content.recipe.answering.checker.IAnsweringChecker;
import indi.wenyan.content.recipe.answering.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.stream.Stream;

class CelestialInkCheckerTest {

    // 提供正确的随机种子与期望的素数个数
    static Stream<Arguments> correctAnswersProvider() {
        return Stream.of(
                // randomVal = 9，代码中加 1 后 target 为 10。
                // 10 以内的素数有 4 个：2, 3, 5, 7
                Arguments.of(9, 4L),

                // randomVal = 19，target 为 20。
                // 20 以内的素数有 8 个：2, 3, 5, 7, 11, 13, 17, 19
                Arguments.of(19, 8L),

                // randomVal = 99，target 为 100。
                // 100 以内的素数有 25 个
                Arguments.of(99, 25L)
        );
    }

    @ParameterizedTest
    @MethodSource("correctAnswersProvider")
    void testCorrectAnswer(int randomVal, long expected) throws WenyanException {
        // 构建模拟的随机源
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(randomVal)
                .build();

        CelestialInkChecker checker = new CelestialInkChecker(random);
        checker.init();

        // 提交正确的答案
        checker.accept(WenyanValues.of(expected));

        // 验证判断结果是否为正确
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // 提供错误的答案以验证查错逻辑
    static Stream<Arguments> wrongAnswersProvider() {
        return Stream.of(
                Arguments.of(9, 5L),   // 10 以内应为 4，故意传 5
                Arguments.of(19, 10L), // 20 以内应为 8，故意传 10
                Arguments.of(99, 0L)   // 100 以内应为 25，故意传 0
        );
    }

    @ParameterizedTest
    @MethodSource("wrongAnswersProvider")
    void testWrongAnswer(int randomVal, long wrongExpected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(randomVal)
                .build();

        CelestialInkChecker checker = new CelestialInkChecker(random);
        checker.init();

        // 提交错误的答案
        checker.accept(WenyanValues.of(wrongExpected));

        // 验证判断结果是否为错误
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}