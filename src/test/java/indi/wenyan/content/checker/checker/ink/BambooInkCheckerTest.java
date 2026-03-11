package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.ink.BambooInkChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BambooInkCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();
        BambooInkChecker checker = new BambooInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("吾有一術"));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();
        BambooInkChecker checker = new BambooInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("其他"));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}