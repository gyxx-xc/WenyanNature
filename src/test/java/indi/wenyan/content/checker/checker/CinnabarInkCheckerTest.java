package indi.wenyan.content.checker.checker;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CinnabarInkCheckerTest {

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 2",
    })
    void testCorrectAnswer(int a, int ans) {
        assertEquals(ans, CinnabarInkChecker.getAns(a));
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        CinnabarInkChecker checker = new CinnabarInkChecker(random);
        checker.init();

        var aValue = checker.getArgs().getAttribute("「甲」");
        int a = aValue.as(WenyanInteger.TYPE).value();

        int day = 1;
        int length = a;
        while (length > 1) {
            length /= 2;
            day++;
        }

        int wrongAns = day + 1;

        checker.accept(WenyanValues.of(wrongAns));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}