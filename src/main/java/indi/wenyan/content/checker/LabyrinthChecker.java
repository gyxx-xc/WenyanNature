package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabyrinthChecker extends CraftingAnswerChecker {
    private final Boolean[][] labyrinth = new Boolean[10][10];

    private enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private int maxDepth = 0;
    private int EndX;
    private int EndY;

    private int curX = 0;
    private int curY = 0;

    public LabyrinthChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);
        for (Boolean[] row : labyrinth) {
            Arrays.fill(row, false); // initialize all cells as walls
        }
        genLabyrinth(0, 0, 0);

        for (var d : Direction.values()) {
            int ni = curX + d.dx;
            int nj = curY + d.dy;
            setVariable(d.ordinal(), new WenyanNativeValue(WenyanType.BOOL, isWall(ni, nj), true));
        }
        setVariable(4, new WenyanNativeValue(WenyanType.INT, EndX, true));
        setVariable(5, new WenyanNativeValue(WenyanType.INT, EndY, true));
    }

    @Override
    public void accept(WenyanNativeValue value) {

    }

    private void genLabyrinth(int i, int j, int depth) {
        if (i < 0 || i >= 10 || j < 0 || j >= 10) {
            return;
        }

        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        // gen a shuffle using nextInt
        for (int k = 0; k < directions.size(); k++) {
            int randomIndex = random.nextInt(directions.size());
            Direction direction = directions.get(randomIndex);
            directions.set(randomIndex, directions.get(k));
            directions.set(k, direction);
        }

        for (Direction direction : directions) {
            int ni = i + direction.dx;
            int nj = j + direction.dy;

            if (!checkWall(ni, nj)) {
                continue;
            }

            labyrinth[i][j] = true;
            genLabyrinth(ni, nj, depth + 1);
            labyrinth[i][j] = false;
        }

        if (depth > maxDepth) {
            maxDepth = depth;
            EndX = i;
            EndY = j;
        }
        labyrinth[i][j] = true;
    }

    private boolean checkWall(int i, int j) {
        if (i < 0 || i >= 10 || j < 0 || j >= 10 || labyrinth[i][j]) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            int ni = i + direction.dx;
            int nj = j + direction.dy;
            if (!isWall(ni, nj)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWall(int i, int j) {
        if (i < 0 || i >= 10 || j < 0 || j >= 10) {
            return true; // out of bounds is considered a wall
        }
        return !labyrinth[i][j];
    }
}
