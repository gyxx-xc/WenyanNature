package indi.wenyan.content.checker;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabyrinthChecker extends CraftingAnswerChecker {
    private record Position(int x, int y) implements WenyanObject {
        public static final Position UP = new Position(-1, 0);
        public static final Position DOWN = new Position(1, 0);
        public static final Position LEFT = new Position(0, -1);
        public static final Position RIGHT = new Position(0, 1);

        public static final List<Position> DIRECTIONS = List.of(UP, DOWN, LEFT, RIGHT);

        private static class OffsetHandler implements JavacallHandler {
            @Override
            public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                if (context.self().casting(WenyanType.OBJECT).getValue() instanceof Position(int x, int y)) {
                    if (context.args().size() == 1 && context.args().getFirst()
                            .casting(WenyanType.OBJECT).getValue() instanceof Position(int dx, int dy)) {
                        return new WenyanNativeValue(WenyanType.OBJECT,
                                new Position(x + dx, y + dy), true);
                    } else {
                        var arg = JavacallHandler.getArgs(context.args(), new WenyanType[]{WenyanType.INT, WenyanType.INT});
                        return new WenyanNativeValue(WenyanType.OBJECT,
                                new Position(x + (int) arg.get(0), y + (int) arg.get(1)), true);
                    }
                } else {
                    throw new WenyanException.WenyanTypeException("Expected Position object");
                }
            }

            @Override
            public boolean isLocal(JavacallContext context) {
                return true;
            }
        }

        @Override
        public WenyanNativeValue getAttribute(String name) {
            return switch (name) {
                case "「「上下」」" -> new WenyanNativeValue(WenyanType.INT, x, true);
                case "「「左右」」" -> new WenyanNativeValue(WenyanType.INT, y, true);
                case "「「偏移」」" -> new WenyanNativeValue(WenyanType.FUNCTION, new OffsetHandler(), true);
                default -> throw new UnsupportedOperationException("Unknown Direction attribute: " + name);
            };
        }

        @Override
        public void setVariable(String name, WenyanNativeValue value) {
            throw new UnsupportedOperationException("Cannot set variable on Direction object: " + name);
        }

        enum PositionType implements WenyanObjectType {
            TYPE;
            @Override
            public WenyanNativeValue getAttribute(String name) {
                return switch (name) {
                    case "「「上」」" -> new WenyanNativeValue(WenyanType.OBJECT, UP, true);
                    case "「「下」」" -> new WenyanNativeValue(WenyanType.OBJECT, DOWN, true);
                    case "「「左」」" -> new WenyanNativeValue(WenyanType.OBJECT, LEFT, true);
                    case "「「右」」" -> new WenyanNativeValue(WenyanType.OBJECT, RIGHT, true);
                    case "「「方向」」" -> new WenyanNativeValue(WenyanType.LIST,
                            new WenyanArrayObject(DIRECTIONS.stream().map(a->new WenyanNativeValue(WenyanType.OBJECT, a, true)).toList()), true);
                    case WenyanDataParser.CONSTRUCTOR_ID -> WenyanValue.NULL;
                    default -> throw new UnsupportedOperationException("Unknown DirectionType attribute: " + name);
                };
            }

            @Override
            public WenyanObject createObject(List<WenyanNativeValue> argsList) throws WenyanException.WenyanTypeException {
                var args = JavacallHandler.getArgs(argsList, new WenyanType[]{WenyanType.INT, WenyanType.INT});
                return new Position((int) args.get(0), (int) args.get(1));
            }
        }
    }

    class Map implements WenyanObject {
        private final int maxX = 10;
        private final int maxY = 10;
        private final Boolean[][] labyrinth = new Boolean[maxX][maxY];
        private int maxDepth = 0;

        private int EndX;
        private int EndY;

        @Override
        public WenyanNativeValue getAttribute(String name) {
            return switch (name) {
                case "「「终」」" -> new WenyanNativeValue(WenyanType.OBJECT, new Position(EndX+1, EndY+1), true);
                case "「「長」」" -> new WenyanNativeValue(WenyanType.INT, maxX, true);
                case "「「寬」」" -> new WenyanNativeValue(WenyanType.INT, maxY, true);
                case "「「尋路」」" -> new WenyanNativeValue(WenyanType.FUNCTION, new JavacallHandler() {
                    @Override
                    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                        if (context.args().size() == 1 && context.args().getFirst()
                                .casting(WenyanType.OBJECT).getValue() instanceof Position(int x, int y)) {
                            return new WenyanNativeValue(WenyanType.BOOL, !isWall(x-1, y-1), true);
                        } else {
                            var arg = JavacallHandler.getArgs(context.args(), new WenyanType[]{WenyanType.INT, WenyanType.INT});
                            return new WenyanNativeValue(WenyanType.BOOL,
                                    !isWall((int) arg.get(0)-1, (int) arg.get(1)-1), true);
                        }
                    }

                    @Override
                    public boolean isLocal(JavacallContext context) {
                        return true;
                    }
                }, true);
                default -> throw new UnsupportedOperationException("Unknown Map attribute: " + name);
            };
        }

        @Override
        public void setVariable(String name, WenyanNativeValue value) {

        }

        private Map() {
            for (Boolean[] row : labyrinth) {
                Arrays.fill(row, false); // initialize all cells as walls
            }
            genLabyrinth(0, 0, 0);
        }

        private void genLabyrinth(int i, int j, int depth) {
            var directions = new ArrayList<>(Position.DIRECTIONS);
            // gen a shuffle using nextInt
            for (int k = 0; k < directions.size(); k++) {
                int randomIndex = random.nextInt(directions.size());
                Position direction = directions.get(randomIndex);
                directions.set(randomIndex, directions.get(k));
                directions.set(k, direction);
            }

            for (Position direction : directions) {
                int ni = i + direction.x;
                int nj = j + direction.y;

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
            if (i < 0 || i >= maxX || j < 0 || j >= maxY || labyrinth[i][j]) {
                return false;
            }
            for (Position direction : Position.DIRECTIONS) {
                int ni = i + direction.x;
                int nj = j + direction.y;
                if (!isWall(ni, nj)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isWall(int i, int j) {
            if (i < 0 || i >= maxX || j < 0 || j >= maxY) {
                return true; // out of bounds is considered a wall
            }
            return !labyrinth[i][j];
        }
    }

    private int curX;
    private int curY;
    private Map input;

    public LabyrinthChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);

        curX = 0;
        curY = 0;
        input = new Map();

        setAttribute("「方位」", new WenyanNativeValue(WenyanType.OBJECT_TYPE,
                Position.PositionType.TYPE, true));

        setAttribute("「迷宫」", new WenyanNativeValue(WenyanType.OBJECT, input, true));
    }

    @Override
    public void accept(WenyanNativeValue value) throws WenyanException.WenyanCheckerError {
        try {
            if (value.casting(WenyanType.OBJECT).getValue() instanceof Position(int dx, int dy)) {
                // TODO: check if Position is direction
                curX = curX + dx;
                curY = curY + dy;

                if (curX == input.EndX && curY == input.EndY) {
                    setStatus(Result.ANSWER_CORRECT);
                    return;
                }

                if (input.isWall(curX, curY)) {
                    setStatus(Result.WRONG_ANSWER);
                    throw new WenyanException.WenyanCheckerError("wrong");
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            setStatus(Result.RUNTIME_ERROR);
            throw new WenyanException(e.getMessage());
        }
    }

    /* one solution
      吾有一列名之曰「圖」
      為是一十遍
      	吾有一列
      	為是一十遍
      		充之以陽
      	云云
      	充「圖」以其
      云云

      吾有一列名之曰「徑」
      有爻陰 名之曰「已尋」

      吾有一術 名之曰「深搜」
      欲行是術 必先得一物曰「己」 乃行是術曰
      書「己」之「「上下」」 以 「己」之「「左右」」
      	若 「己」之「「上下」」等於「迷宫」之「「终」」之「「上下」」 者
      	若 「己」之「「左右」」等於「迷宫」之「「终」」之「「左右」」 者
      		昔之「已尋」者 今陽是矣
      		凡「徑」中之「向」
      			書「向」
      		云云
      		乃歸空無
      	云云也

      	吾有一列 銜其以「徑」 名之曰「舊」
      	凡 「方位」之「「方向」」 中之「向」
      		施「己」之「「偏移」」於「向」 名之曰「新己」

      		夫「新己」之「「上下」」 名之曰「上下」
      		夫「新己」之「「左右」」 名之曰「左右」

      		施「迷宫」之「「尋路」」以「新己」
      		若其者
      		若「圖」之「上下」之「左右」者
      			昔之 「圖」之「上下」之「左右」 者 今陰是矣
      			充「徑」以「向」
      			施「深搜」以「新己」
      			若「已尋」者 乃歸空無 云云
      			夫「舊」 名之曰「徑」
      		云云也
      	云云
      是謂「深搜」之術也

      昔之「圖」之一之一者今陰是矣
      造「方位」以一以一
      施「深搜」於其
     */

    /*
     吾有一列名之曰「徑」
      有爻陰 名之曰「已尋」

      吾有一術 名之曰「深搜」
      欲行是術 必先得一物曰「己」 乃行是術曰
      	若 「己」之「「上下」」等於「迷宫」之「「终」」之「「上下」」 者
      	若 「己」之「「左右」」等於「迷宫」之「「终」」之「「左右」」 者
      		昔之「已尋」者 今陽是矣
      		凡「徑」中之「向」
      			書「向」
      		云云
      		乃歸空無
      	云云也

      	吾有一列 銜其以「徑」 名之曰「舊」
      	凡 「方位」之「「方向」」 中之「向」
      		施「己」之「「偏移」」於「向」 名之曰「新己」

      		施「迷宫」之「「尋路」」以「新己」
      		若其者
      		施「記」之「「標記乎」」於「新己」
      		若其者
      			施「記」之「「標記」」於「新己」
      			充「徑」以「向」
      			施「深搜」以「新己」
      			若「已尋」者 乃歸空無 云云
      			夫「舊」 名之曰「徑」
      		云云也
      	云云
      是謂「深搜」之術也

      吾有一物名之曰「標記」其物如是
      	物之造者術 是術曰
      		吾有一列 名之曰己之「「圖」」
      		為是一十遍
      			吾有一列
      			為是一十遍
      				充之以陽
      			云云
      			充己之「「圖」」以其
      		云云
      	是謂造之術也

      	物之「「標記乎」」者術
      	欲行是術 必先得一物曰「己」
      	乃行是術曰
      		夫「新己」之「「上下」」 名之曰「上下」
      		夫「新己」之「「左右」」 名之曰「左右」
      		乃得 己之「「圖」」之「上下」之「左右」
      	是謂「「標記乎」」之術也

      	物之「「標記」」者術
      	欲行是術 必先得一物曰「己」
      	乃行是術曰
      		夫「新己」之「「上下」」 名之曰「上下」
      		夫「新己」之「「左右」」 名之曰「左右」
      		昔之 己之「「圖」」之「上下」之「左右」 者 今陰是矣
      	是謂「「標記」」之術也
      是謂「標記」之物也

      造「標記」名之曰「記」
      造「方位」以一以一
      施「記」之「「標記」」於之
      施「深搜」於其
     */
}
