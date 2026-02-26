package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class HandRunner2CheckerTest {

    @Test
    void testCorrectAnswer() throws Exception {
        RandomSource random = RandomSource.create();
        HandRunner2Checker checker = new HandRunner2Checker(random);
        checker.init();

        Field expectedAvgField = HandRunner2Checker.class.getDeclaredField("expectedAvg");
        expectedAvgField.setAccessible(true);
        double expectedAvg = (double) expectedAvgField.get(checker);

        checker.accept(WenyanValues.of(expectedAvg));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws Exception {
        RandomSource random = RandomSource.create();
        HandRunner2Checker checker = new HandRunner2Checker(random);
        checker.init();

        Field expectedAvgField = HandRunner2Checker.class.getDeclaredField("expectedAvg");
        expectedAvgField.setAccessible(true);
        double expectedAvg = (double) expectedAvgField.get(checker);

        double wrongAns = expectedAvg + 1.0;

        checker.accept(WenyanValues.of(wrongAns));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}