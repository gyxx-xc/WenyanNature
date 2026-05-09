package indi.wenyan.content.recipe.answering.checker.checker.paper;

import indi.wenyan.content.recipe.answering.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.function.WenyanValues;
import net.minecraft.util.RandomSource;

public class PhoenixPaperChecker extends ValueAnswerChecker {
    private int solutionCount = 0;

    public PhoenixPaperChecker(RandomSource random) {
        super(random);
    }

    private void solve(int row, int n, int cols, int diag1, int diag2) {
        if (row == n) {
            solutionCount++;
            return;
        }
        int availablePositions = ((1 << n) - 1) & (~(cols | diag1 | diag2));

        while (availablePositions != 0) {
            int position = availablePositions & (-availablePositions);
            availablePositions &= (availablePositions - 1);
            solve(row + 1, n, cols | position, (diag1 | position) << 1, (diag2 | position) >> 1);
        }
    }

    @Override
    public void init() {
        super.init();

        int n = random.nextInt(5) + 4;
        setVariable(0, WenyanValues.of(n));

        solutionCount = 0;
        solve(0, n, 0, 0, 0);

        ans = WenyanValues.of(solutionCount);
    }
}