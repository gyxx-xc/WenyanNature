package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StarlightInkCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "0, 1",
            "1, 3",
            "2, 9",
            "3, 33",
            "4, 153",
            "19, 2561327494111820313"
    })
    void testCorrectAnswer(int input, long expectedSum) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input)
                .build();
        StarlightInkChecker checker = new StarlightInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expectedSum));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2",
            "1, 4",
            "2, 10"
    })
    void testWrongAnswer(int input, long wrongSum) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input)
                .build();
        StarlightInkChecker checker = new StarlightInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongSum));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}