package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandRunner6CheckerTest {

    @ParameterizedTest
    @CsvSource({
            "3, 7, 5",
            "5, 11, 9",
            "2, 5, 3",
            "7, 4, 3",
            "9, 2, 1",
            "2, 4, -1",
            "6, 9, -1",
            "4, 6, -1",
            "10, 15, -1",
    })
    void testCorrectAnswer(int a, int m, int expectedX) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(a)
                .addLong(m)
                .build();

        IAnsweringChecker checker = new HandRunner6Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(expectedX));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "3, 7, 4",     // not 5
            "5, 11, 8",    // not 9
            "2, 5, 2",     // not 3
            "2, 4, 2",     // gcd≠1, not -1
            "6, 9, 3",     // gcd≠1, not -1
            "7, 4, 2",     // not 3
            "9, 2, 2"      // not 1
    })
    void testWrongAnswer(int a, int m, int wrongX) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(a)
                .addLong(m)
                .build();

        IAnsweringChecker checker = new HandRunner6Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongX));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }


    @Test
    void testZeroAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(3)
                .addLong(7)
                .build();

        IAnsweringChecker checker = new HandRunner6Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(0));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testNegativeAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(3)
                .addLong(7)
                .build();

        IAnsweringChecker checker = new HandRunner6Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(-2));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testOffByOne() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(3)
                .addLong(7)
                .build();

        IAnsweringChecker checker = new HandRunner6Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(4)); // 5-1
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}
