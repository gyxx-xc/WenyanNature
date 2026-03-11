package indi.wenyan.content.checker.checker;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.handrunner.HandRunner2Checker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandRunner2CheckerTest {

    @Test
    void testCorrectAnswer() throws Exception {
        // n = 5 (input = 5 - 3 = 2)
        // sequence of scores: 1, 2, 3, 4, 5
        // calculation: max=5, min=1. sum=15. avg = (15 - 5 - 1) / (5 - 2) = 9 / 3 = 3.0
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(2, 1, 2, 3, 4, 5)
                .build();
        HandRunner2Checker checker = new HandRunner2Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(3.0));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws Exception {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .addSeq(2, 1, 2, 3, 4, 5)
                .build();
        HandRunner2Checker checker = new HandRunner2Checker(random);
        checker.init();

        checker.accept(WenyanValues.of(4.0));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}