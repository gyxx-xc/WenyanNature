package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudPaperCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        CloudPaperChecker checker = new CloudPaperChecker(random);
        checker.init();

        var kValue = checker.getArgs().getAttribute("「甲」");
        int k = kValue.as(WenyanInteger.TYPE).value();

        long totalCoins = 0;
        int currentDay = 1;
        int coinsPerDay = 1;

        while (currentDay <= k) {
            for (int i = 0; i < coinsPerDay && currentDay <= k; i++) {
                totalCoins += coinsPerDay;
                currentDay++;
            }
            coinsPerDay++;
        }

        checker.accept(WenyanValues.of(totalCoins));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testWrongAnswer() throws WenyanException {
        RandomSource random = RandomSource.create();
        CloudPaperChecker checker = new CloudPaperChecker(random);
        checker.init();

        var kValue = checker.getArgs().getAttribute("「甲」");
        int k = kValue.as(WenyanInteger.TYPE).value();

        long totalCoins = 0;
        int currentDay = 1;
        int coinsPerDay = 1;

        while (currentDay <= k) {
            for (int i = 0; i < coinsPerDay && currentDay <= k; i++) {
                totalCoins += coinsPerDay;
                currentDay++;
            }
            coinsPerDay++;
        }

        long wrongAns = totalCoins + 1;

        checker.accept(WenyanValues.of(wrongAns));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}