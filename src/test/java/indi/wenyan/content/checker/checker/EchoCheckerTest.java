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

class EchoCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "42, 42",
            "-1, -1",
            "0, 0"
    })
    void testCorrectAnswer(int input, int expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input)
                .build();
        EchoChecker checker = new EchoChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "42, 43",
            "-1, 0",
            "0, 1"
    })
    void testWrongAnswer(int input, int wrong) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input)
                .build();
        EchoChecker checker = new EchoChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrong));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}