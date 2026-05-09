package indi.wenyan.content.recipe.answering.checker.checker.paper;

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

class DragonPaperCheckerTest {

    // 提供正确的随机序列与期望值
    static Stream<Arguments> correctAnswersProvider() {
        return Stream.of(
                // 用例1：n=2, m=2。边：1->2 (权10), 2->1 (权20)。去程10+回程20=30
                // 序列解析：n(1+1), m(1+1), 树边(u=1, w=10), 剩余边(u=2, v=1, w=20)
                Arguments.of(new int[]{1, 1, 0, 9, 1, 0, 19}, 30L),

                // 用例2：n=3, m=3。边：1->2(权10), 2->3(权20), 3->1(权30)。总和120
                // 序列解析：n(2+1), m(1+(3-1)), 树边2(0,9), 树边3(1,19), 剩余边(2,0,29)
                Arguments.of(new int[]{2, 1, 0, 9, 1, 19, 2, 0, 29}, 120L)
        );
    }

    @ParameterizedTest
    @MethodSource("correctAnswersProvider")
    void testCorrectAnswer(int[] randomSeq, long expected) throws WenyanException {
        MockRandomSource.InputBuilder builder = MockRandomSource.InputBuilder.create();
        // 遍历数组，逐个添加随机数序列
        for (int num : randomSeq) {
            builder.addSeq(num);
        }
        RandomSource random = builder.build();

        DragonPaperChecker checker = new DragonPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }
    // 提供必定导致错误的答案
    static Stream<Arguments> wrongAnswersProvider() {
        return Stream.of(
                Arguments.of(new int[]{1, 1, 0, 9, 1, 0, 19}, 999L), // 实际应为30
                Arguments.of(new int[]{2, 1, 0, 9, 1, 19, 2, 0, 29}, 0L) // 实际应为120
        );
    }

    @ParameterizedTest
    @MethodSource("wrongAnswersProvider")
    void testWrongAnswer(int[] randomSeq, long wrongExpected) throws WenyanException {
        MockRandomSource.InputBuilder builder = MockRandomSource.InputBuilder.create();
        // 同样遍历数组逐个添加
        for (int num : randomSeq) {
            builder.addSeq(num);
        }
        RandomSource random = builder.build();

        DragonPaperChecker checker = new DragonPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongExpected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}
