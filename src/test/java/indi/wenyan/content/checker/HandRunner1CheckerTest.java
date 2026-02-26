package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandRunner1CheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        HandRunner1Checker checker = new HandRunner1Checker(random);
        checker.init();

        int a = checker.getArgs().getAttribute("「甲」").as(WenyanInteger.TYPE).value();
        int b = checker.getArgs().getAttribute("「乙」").as(WenyanInteger.TYPE).value();

        checker.accept(WenyanValues.of(a > b));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        HandRunner1Checker checker = new HandRunner1Checker(random);
        checker.init();

        int a = checker.getArgs().getAttribute("「甲」").as(WenyanInteger.TYPE).value();
        int b = checker.getArgs().getAttribute("「乙」").as(WenyanInteger.TYPE).value();

        checker.accept(WenyanValues.of(a <= b)); // Wrong answer
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}