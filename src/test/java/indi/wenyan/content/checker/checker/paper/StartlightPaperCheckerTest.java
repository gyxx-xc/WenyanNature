package indi.wenyan.content.checker.checker.paper;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link StartlightPaperChecker}.
 *
 * The checker's random call order in init():
 *   1. nextInt(20)  → rowOffset  → row = rowOffset + 1
 *   2. nextInt(20)  → colOffset  → col = colOffset + 1
 *   3. row*col calls of nextInt(99) → cell values (val = raw + 1)
 *
 * The answer is [maxRow, maxCol] (0-indexed) of the maximum value in the grid.
 *
 * MockRandomSource.nextInt(bound) ignores the bound and returns the next raw value.
 * So addSeq values map 1-to-1 to the raw values consumed by nextInt calls.
 *
 * Test case derivations (rowOffset, colOffset, ...cells):
 *
 *  Case A: 1×1 grid
 *    seq: 0, 0, 5  → row=1, col=1, cell[0][0]=6
 *    max at (0, 0)
 *
 *  Case B: 2×2 grid, max at bottom-right
 *    seq: 1, 1, 3, 7, 10, 50  → row=2, col=2
 *    cells: [0][0]=4, [0][1]=8, [1][0]=11, [1][1]=51
 *    max=51 at (1, 1)
 *
 *  Case C: 2×3 grid, max in first row middle
 *    seq: 1, 2, 5, 80, 10, 1, 2, 3  → row=2, col=3
 *    cells: [0][0]=6, [0][1]=81, [0][2]=11, [1][0]=2, [1][1]=3, [1][2]=4
 *    max=81 at (0, 1)
 *
 *  Case D: 3×1 grid, max in last row
 *    seq: 2, 0, 10, 20, 60  → row=3, col=1
 *    cells: [0][0]=11, [1][0]=21, [2][0]=61
 *    max=61 at (2, 0)
 */
class StartlightPaperCheckerTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static WenyanList listOf(int a, int b) {
        WenyanList list = new WenyanList();
        list.add(WenyanValues.of(a));
        list.add(WenyanValues.of(b));
        return list;
    }

    private static RandomSource buildRandom(Object... seq) {
        return MockRandomSource.InputBuilder.create().addSeq(seq).build();
    }

    // Case A  (1×1)
    @ParameterizedTest
    @CsvSource("0, 0, 0, 0")
    void testCorrectAnswer_1x1(int expectedRow, int expectedCol) throws WenyanException {
        // row=1, col=1, single cell value raw=5 → 6
        RandomSource random = buildRandom(0, 0, 5);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(listOf(expectedRow, expectedCol));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // Case B  (2×2, max at bottom-right)
    @ParameterizedTest
    @CsvSource("1, 1")
    void testCorrectAnswer_2x2_maxBottomRight(int expectedRow, int expectedCol) throws WenyanException {
        // row=2, col=2
        // cells: [0][0]=4, [0][1]=8, [1][0]=11, [1][1]=51
        RandomSource random = buildRandom(1, 1, 3, 7, 10, 50);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(listOf(expectedRow, expectedCol));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // Case C  (2×3, max in first row)
    @ParameterizedTest
    @CsvSource("0, 1")
    void testCorrectAnswer_2x3_maxFirstRow(int expectedRow, int expectedCol) throws WenyanException {
        // row=2, col=3
        // cells: [0][0]=6, [0][1]=81, [0][2]=11, [1][0]=2, [1][1]=3, [1][2]=4
        RandomSource random = buildRandom(1, 2, 5, 80, 10, 1, 2, 3);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(listOf(expectedRow, expectedCol));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // Case D  (3×1, max in last row)
    @ParameterizedTest
    @CsvSource("2, 0")
    void testCorrectAnswer_3x1_maxLastRow(int expectedRow, int expectedCol) throws WenyanException {
        // row=3, col=1
        // cells: [0][0]=11, [1][0]=21, [2][0]=61
        RandomSource random = buildRandom(2, 0, 10, 20, 60);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(listOf(expectedRow, expectedCol));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    // ── Wrong answer ──────────────────────────────────────────────────────────

    /**
     * Uses Case B (2×2 grid, correct answer = [1,1]).
     * Submitting any other [row,col] pair should yield WRONG_ANSWER.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "0, 1",
            "1, 0"
    })
    void testWrongAnswer(int wrongRow, int wrongCol) throws WenyanException {
        // row=2, col=2; cells: [0][0]=4, [0][1]=8, [1][0]=11, [1][1]=51  → correct=(1,1)
        RandomSource random = buildRandom(1, 1, 3, 7, 10, 50);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(listOf(wrongRow, wrongCol));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    // ── Wrong type ────────────────────────────────────────────────────────────

    /**
     * Submitting a plain integer (not a list) should yield WRONG_ANSWER.
     */
    @ParameterizedTest
    @CsvSource({
            "0",
            "1",
            "42"
    })
    void testWrongType_scalar(int wrongValue) throws WenyanException {
        RandomSource random = buildRandom(1, 1, 3, 7, 10, 50);
        StartlightPaperChecker checker = new StartlightPaperChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongValue));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}