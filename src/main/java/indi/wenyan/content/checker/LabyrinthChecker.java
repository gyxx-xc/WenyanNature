package indi.wenyan.content.checker;

import indi.wenyan.content.handler.WenyanBuiltinFunction;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabyrinthChecker extends CraftingAnswerChecker {
    private record Position(int x, int y) implements IWenyanObject {
        public static final WenyanType<Position> TYPE = new WenyanType<>("position", Position.class);
        public static final Position UP = new Position(-1, 0);
        public static final Position DOWN = new Position(1, 0);
        public static final Position LEFT = new Position(0, -1);
        public static final Position RIGHT = new Position(0, 1);

        public static final List<Position> DIRECTIONS = List.of(UP, DOWN, LEFT, RIGHT);

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        @Override
        public IWenyanValue getAttribute(String name) {
            return switch (name) {
                case "「上下」" -> WenyanValues.of(x);
                case "「左右」" -> WenyanValues.of(y);
                case "「偏移」" -> new WenyanBuiltinFunction(((self, args) -> {
                    if (args.size() == 1 && args.getFirst().as(TYPE) instanceof Position(int dx, int dy)) {
                        return new Position(self.as(TYPE).x + dx, self.as(TYPE).y + dy);
                    } else {
                        return new Position(self.as(TYPE).x + args.get(0).as(WenyanInteger.TYPE).value(),
                                self.as(TYPE).y + args.get(1).as(WenyanInteger.TYPE).value());
                    }
                }));
                default -> throw new UnsupportedOperationException("Unknown Direction attribute: " + name);
            };
        }

        @Override
        public void setVariable(String name, IWenyanValue value) {
            throw new UnsupportedOperationException("Cannot set variable on Direction object: " + name);
        }

        enum PositionType implements IWenyanObjectType {
            POSITION_TYPE;
            public static final WenyanType<PositionType> TYPE = new WenyanType<>("position_type", PositionType.class);

            @Override
            public IWenyanValue getAttribute(String name) {
                return switch (name) {
                    case "「上」" -> UP;
                    case "「下」" -> DOWN;
                    case "「左」" -> LEFT;
                    case "「右」" -> RIGHT;
                    case "「方向」" -> WenyanValues.of(List.copyOf(DIRECTIONS));
                    case WenyanDataParser.CONSTRUCTOR_ID -> WenyanNull.NULL;
                    default -> throw new UnsupportedOperationException("Unknown DirectionType attribute: " + name);
                };
            }

            @Override
            public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException.WenyanTypeException {
                return new Position(argsList.get(0).as(WenyanInteger.TYPE).value(),
                        argsList.get(1).as(WenyanInteger.TYPE).value());
            }

            @Override
            public WenyanType<?> type() {
                return TYPE;
            }
        }
    }

    class Map implements IWenyanObject {
        public static final WenyanType<Map> TYPE = new WenyanType<>("map", Map.class);
        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        private final int maxX = 10;
        private final int maxY = 10;
        private final Boolean[][] labyrinth = new Boolean[maxX][maxY];
        private int maxDepth = 0;

        private int EndX;
        private int EndY;

        @Override
        public IWenyanValue getAttribute(String name) {
            return switch (name) {
                case "「终」" -> new Position(EndX + 1, EndY + 1);
                case "「長」" -> WenyanValues.of(maxX);
                case "「寬」" -> WenyanValues.of(maxY);
                case "「尋路」" -> new WenyanBuiltinFunction(((self, args) -> {
                    if (args.size() == 1 && args.getFirst().as(Position.TYPE) instanceof Position(int x, int y)) {
                        return WenyanValues.of(!isWall(x - 1, y - 1));
                    } else {
                        return WenyanValues.of(!isWall(args.get(0).as(WenyanInteger.TYPE).value() - 1,
                                args.get(1).as(WenyanInteger.TYPE).value() - 1));
                    }
                }));
                default -> throw new UnsupportedOperationException("Unknown Map attribute: " + name);
            };
        }

        @Override
        public void setVariable(String name, IWenyanValue value) {

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

        setAttribute("「方位」", Position.PositionType.POSITION_TYPE);

        setAttribute("「迷宫」", input);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException.WenyanCheckerError {
        try {
            if (value.as(Position.TYPE) instanceof Position(int dx, int dy)) {
                if (Math.abs(dx) + Math.abs(dy) != 1) {
                    setStatus(Result.WRONG_ANSWER);
                    throw new WenyanException.WenyanCheckerError("wrong");
                }

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
