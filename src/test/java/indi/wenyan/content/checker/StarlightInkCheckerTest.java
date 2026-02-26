package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StarlightInkCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        StarlightInkChecker checker = new StarlightInkChecker(random);
        checker.init();

        int n = checker.getArgs().getAttribute("「甲」").as(WenyanInteger.TYPE).value();

        long sum = 0L;
        long factorial = 1L;
        for (int i = 1; i <= n; i++) {
            factorial = factorial * i;
            sum = sum + factorial;
        }

        checker.accept(WenyanValues.of(sum));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        StarlightInkChecker checker = new StarlightInkChecker(random);
        checker.init();

        var nValue = checker.getArgs().getAttribute("「甲」");
        int n = nValue.as(WenyanInteger.TYPE).value();

        Long sum = 0L;
        Long factorial = 1L;
        for (int i = 1; i <= n; i++) {
            factorial = factorial * i;
            sum = sum + factorial;
        }

        Long wrongAns = sum + 1L;

        checker.accept(WenyanValues.of(wrongAns));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}