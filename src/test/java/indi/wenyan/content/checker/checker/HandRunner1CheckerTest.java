package indi.wenyan.content.checker.checker;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandRunner1CheckerTest {

    @ParameterizedTest
    @CsvSource({
            "10, 5, true",
            "5, 10, false",
            "5, 5, false"
    })
    void testCorrectAnswer(int a, int b, boolean expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(a, b)
                .build();
        HandRunner1Checker checker = new HandRunner1Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "10, 5, false",
            "5, 10, true",
            "5, 5, true"
    })
    void testWrongAnswer(int a, int b, boolean wrong) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(a, b)
                .build();
        HandRunner1Checker checker = new HandRunner1Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrong));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}