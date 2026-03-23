package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HandRunner4CheckerTest {
    @ParameterizedTest
    @CsvSource({
            "39, 200, 4, false",
    })
    void testCorrectAnswer(int n, boolean expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        HandRunner3Checker checker = new HandRunner3Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "",
            "2, false",
            "3, false",
            "4, true",
            "5, false",
            "91, true",
            "97, false"
    })
    void testWrongAnswer(int n, boolean wrongAnswer) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(n - 1)
                .build();
        HandRunner3Checker checker = new HandRunner3Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongAnswer));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}
