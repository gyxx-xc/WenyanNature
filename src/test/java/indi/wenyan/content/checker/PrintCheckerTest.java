package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrintCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        PrintChecker checker = new PrintChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("吾有一術"));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        PrintChecker checker = new PrintChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("其他"));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}