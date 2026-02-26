package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EchoCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        EchoChecker checker = new EchoChecker(random);
        checker.init();

        IWenyanValue ans = checker.getArgs().getAttribute("「甲」");
        checker.accept(ans);
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        EchoChecker checker = new EchoChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("错误答案"));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}