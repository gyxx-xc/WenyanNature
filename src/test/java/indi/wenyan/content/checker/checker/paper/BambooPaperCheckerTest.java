package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.paper.BambooPaperChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BambooPaperCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "10, 20, 30",
            "0, 0, 0",
            "99, 1, 100"
    })
    void testCorrectAnswer(int a, int b, long expected) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(a, b)
                .build();
        BambooPaperChecker checker = new BambooPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expected));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "10, 20, 31",
            "0, 0, 1",
            "99, 1, 99"
    })
    void testWrongAnswer(int a, int b, long wrong) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(a, b)
                .build();
        BambooPaperChecker checker = new BambooPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrong));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}