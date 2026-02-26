package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanDouble;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * HandRunner2Checker verifies if the user correctly implemented the score
 * calculation.
 */
public class HandRunner2Checker extends CraftingAnswerChecker {
    private double expectedAvg;

    public HandRunner2Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int n = random.nextInt(998) + 3; // 3 <= n <= 1000
        setVariable(0, WenyanValues.of(n));

        List<IWenyanValue> scores = new ArrayList<>();
        int sum = 0;
        int max = -1;
        int min = 11;

        for (int i = 0; i < n; i++) {
            int score = random.nextInt(11); // 0 to 10
            scores.add(WenyanValues.of(score));
            sum += score;
            if (score > max) {
                max = score;
            }
            if (score < min) {
                min = score;
            }
        }

        setVariable(1, WenyanValues.of(scores));

        // Calculate average after removing one max and one min score
        expectedAvg = (double) (sum - max - min) / (n - 2);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException {
        try {
            // Convert any numeric value (or left value wrapper) to double safely
            double userVal = value.as(WenyanDouble.TYPE).value();
            if (Math.abs(userVal - expectedAvg) < 0.01) {
                setResult(ResultStatus.ANSWER_CORRECT);
            } else {
                setResult(ResultStatus.WRONG_ANSWER);
            }
        } catch (WenyanException e) {
            setResult(ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
    }
}
