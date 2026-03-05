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

class CloudPaperCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "6, 14",
            "1000, 29820",
    })
    void testCorrectAnswer(int input, long expectedCoins) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input - 1)
                .build();
        CloudPaperChecker checker = new CloudPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(expectedCoins));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2",
            "1, 4",
            "2, 6",
            "3, 9",
            "9, 31"
    })
    void testWrongAnswer(int input, long wrongCoins) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(input - 1)
                .build();
        CloudPaperChecker checker = new CloudPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongCoins));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}