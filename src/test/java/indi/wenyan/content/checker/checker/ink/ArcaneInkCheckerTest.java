package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArcaneInkCheckerTest {
    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "2, 6",
    })
    void testCorrectAnswer(int n, String expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        ArcaneInkChecker checker = new ArcaneInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 3",
            "2, 7"
    })
    void testWrongAnswer(int n, String wrongAnswer) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        ArcaneInkChecker checker = new ArcaneInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongAnswer));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}