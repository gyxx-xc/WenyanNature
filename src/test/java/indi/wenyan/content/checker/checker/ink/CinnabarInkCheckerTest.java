package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CinnabarInkCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 2",
            "4, 3",
            "8, 4",
            "17, 5",
            "100, 7",
            "1024, 11",
            "1000000000, 30"
    })
    void testCorrectAnswer(long input, int expectedDays) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(input - 1)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expectedDays));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "2, 3",
            "4, 4",
            "8, 5",
            "16, 6",
            "100, 8",
            "1024, 12",
            "1000000000, 31"
    })
    void testWrongAnswer(long input, int wrongDays) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(input - 1)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongDays));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testInitialization() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(100L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        assertNotNull(checker.getArgs(), "Arguments should not be null after initialization");
        assertNotNull(checker.getArgs().getAttribute("「甲」"), "Variable '甲' should be set");
        assertEquals(IAnsweringChecker.ResultStatus.RUNNING, checker.getResult(),
                "Result status should be RUNNING after initialization");
    }

    @Test
    void testMinimumInput() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(0L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        long a = checker.getArgs().getAttribute("「甲」").as(WenyanInteger.TYPE).value();
        assertEquals(1L, a);

        checker.accept(WenyanValues.of(1L));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testMaximumInput() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(1_000_000_000L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        long a = checker.getArgs().getAttribute("「甲」").as(WenyanInteger.TYPE).value();
        assertEquals(1_000_000_001L, a);

        checker.accept(WenyanValues.of(30L));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testZeroAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(100L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(0L));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testNegativeAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(100L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(-1L));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testOffByOne() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(8L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(3L));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());

        checker.accept(WenyanValues.of(5L));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testPowerOfTwo() throws WenyanException {
        long[] powersOfTwo = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
        int[] expectedDays = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        for (int i = 0; i < powersOfTwo.length; i++) {
            RandomSource random = MockRandomSource.InputBuilder.create()
                    .addLong(powersOfTwo[i])
                    .build();

            IAnsweringChecker checker = new CinnabarInkChecker(random);
            checker.init();

            checker.accept(WenyanValues.of(expectedDays[i]));
            assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult(),
                    "Failed for input " + powersOfTwo[i]);
        }
    }

    @Test
    void testNonPowerOfTwo() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(15L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(5L));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testResultStatusTransition() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addLong(100L)
                .build();

        IAnsweringChecker checker = new CinnabarInkChecker(random);

        assertNull(checker.getResult(), "Should be null before init");

        checker.init();
        assertEquals(IAnsweringChecker.ResultStatus.RUNNING, checker.getResult(),
                "Should be RUNNING after init");

        checker.accept(WenyanValues.of(7L));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult(),
                "Should be ANSWER_CORRECT after correct answer");
    }
}
