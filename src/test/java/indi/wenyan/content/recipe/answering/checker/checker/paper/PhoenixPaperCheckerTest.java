package indi.wenyan.content.recipe.answering.checker.checker.paper;

import indi.wenyan.content.recipe.answering.checker.IAnsweringChecker;
import indi.wenyan.content.recipe.answering.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class PhoenixPaperCheckerTest {

    static Stream<Arguments> correctAnswersProvider() {
        return Stream.of(
                // randomVal = 0，N = 4，4皇后有 2 种方案
                Arguments.of(0, 2L),
                // randomVal = 4，N = 8，8皇后有 92 种方案
                Arguments.of(4, 92L),
                // randomVal = 6，N = 10，10皇后有 724 种方案
                Arguments.of(6, 724L)
        );
    }

    @ParameterizedTest
    @MethodSource("correctAnswersProvider")
    void testCorrectAnswer(int randomVal, long expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(randomVal)
                .build();

        PhoenixPaperChecker checker = new PhoenixPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    static Stream<Arguments> wrongAnswersProvider() {
        return Stream.of(
                Arguments.of(0, 5L),   // N=4 时错填为 5
                Arguments.of(4, 100L), // N=8 时错填为 100
                Arguments.of(6, 0L)    // N=10 时错填为 0
        );
    }

    @ParameterizedTest
    @MethodSource("wrongAnswersProvider")
    void testWrongAnswer(int randomVal, long wrongExpected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(randomVal)
                .build();

        PhoenixPaperChecker checker = new PhoenixPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongExpected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}