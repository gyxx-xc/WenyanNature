package indi.wenyan.content.recipe.answering.checker.wyquestion.ink;

import indi.wenyan.content.recipe.answering.checker.IAnsweringChecker;
import indi.wenyan.content.recipe.answering.checker.wyquestion.test_utils.MockRandomSource;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.exception.WenyanException;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LunarInkCheckerTest {
    @ParameterizedTest
    @CsvSource({
            "16, 4",
            "5, 3",
    })
    void testCorrectAnswer(int n, int expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        LunarInkChecker checker = new LunarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 15",
            "2, 15"
    })
    void testWrongAnswer(int n, int wrongAnswer) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        LunarInkChecker checker = new LunarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongAnswer));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}