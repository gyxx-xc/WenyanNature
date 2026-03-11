package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.ink.StarlightInkChecker;
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
            "0, 1", // n = 1 (input=0) -> 1! = 1
            "1, 3", // n = 2 (input=1) -> 1! + 2! = 3
            "2, 9", // n = 3 (input=2) -> 1! + 2! + 3! = 9
            "3, 33", // n = 4 (input=3) -> 1! + 2! + 3! + 4! = 33
            "4, 153" // n = 5 (input=4) -> 1! + 2! + 3! + 4! + 5! = 153
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