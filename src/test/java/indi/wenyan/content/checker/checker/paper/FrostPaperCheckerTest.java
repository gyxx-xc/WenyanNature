package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FrostPaperCheckerTest {
    @ParameterizedTest
    @CsvSource({
            "1, 2",
            "2, 6"
    })
    void testCorrectAnswer(int input, int expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input-1)
                .build();
        FrostPaperChecker checker = new FrostPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 3",
            "2, 10"

    })
    void testWrongAnswer(int input, int expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input-1)
                .build();
        FrostPaperChecker checker = new FrostPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}