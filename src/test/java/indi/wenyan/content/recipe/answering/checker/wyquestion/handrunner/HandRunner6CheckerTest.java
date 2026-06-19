package indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner;

import indi.wenyan.content.recipe.answering.checker.IAnsweringChecker;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

class HandRunner6CheckerTest {

    @Test
    void testCorrectPath() throws Exception {
        RandomSource random = RandomSource.create(42);
        HandRunner6Checker checker = new HandRunner6Checker(random);
        checker.init();

        // 1. 获取地图与终点信息
        Field inputField = HandRunner6Checker.class.getDeclaredField("input");
        inputField.setAccessible(true);
        Object map = inputField.get(checker);

        Field endXField = map.getClass().getDeclaredField("endX");
        endXField.setAccessible(true);
        int endX = endXField.getInt(map);

        Field endYField = map.getClass().getDeclaredField("endY");
        endYField.setAccessible(true);
        int endY = endYField.getInt(map);

        Method isWallMethod = map.getClass().getDeclaredMethod("isWall", int.class, int.class);
        isWallMethod.setAccessible(true);

        // 2. 通过反射直接获取 Position 中的方向常量
        Class<?> posClass = Class.forName("indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner.HandRunner6Checker$Position");
        Field upField = posClass.getDeclaredField("UP"); upField.setAccessible(true);
        Field downField = posClass.getDeclaredField("DOWN"); downField.setAccessible(true);
        Field leftField = posClass.getDeclaredField("LEFT"); leftField.setAccessible(true);
        Field rightField = posClass.getDeclaredField("RIGHT"); rightField.setAccessible(true);

        IWenyanValue UP = (IWenyanValue) upField.get(null);
        IWenyanValue DOWN = (IWenyanValue) downField.get(null);
        IWenyanValue LEFT = (IWenyanValue) leftField.get(null);
        IWenyanValue RIGHT = (IWenyanValue) rightField.get(null);

        // 3. BFS 自动寻路
        Queue<int[]> queue = new LinkedList<>();
        Map<String, IWenyanValue> parentStep = new HashMap<>();
        Map<String, String> parentNode = new HashMap<>();

        queue.add(new int[]{0, 0});
        parentNode.put("0,0", null);

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        IWenyanValue[] dirValues = {UP, DOWN, LEFT, RIGHT};

        boolean found = false;
        while (!queue.isEmpty() && !found) {
            int[] cur = queue.poll();
            int cx = cur[0], cy = cur[1];

            for (int i = 0; i < 4; i++) {
                int nx = cx + dirs[i][0];
                int ny = cy + dirs[i][1];
                String nKey = nx + "," + ny;

                if (!parentNode.containsKey(nKey)) {
                    boolean isWall = (boolean) isWallMethod.invoke(map, nx, ny);
                    if (!isWall) {
                        parentNode.put(nKey, cx + "," + cy);
                        parentStep.put(nKey, dirValues[i]);
                        queue.add(new int[]{nx, ny});

                        if (nx == endX && ny == endY) {
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        // 4. 回溯路径并验证
        List<IWenyanValue> correctPath = new ArrayList<>();
        String currKey = endX + "," + endY;
        while (currKey != null && !currKey.equals("0,0")) {
            correctPath.addFirst(parentStep.get(currKey));
            currKey = parentNode.get(currKey);
        }

        for (IWenyanValue step : correctPath) {
            checker.accept(step);
        }
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @Test
    void testHitWallWrongAnswer() throws Exception {
        RandomSource random = RandomSource.create(100);
        HandRunner6Checker checker = new HandRunner6Checker(random);
        checker.init();

        // 原点 (0,0) 往上 (-1,0) 必撞墙越界
        Class<?> posClass = Class.forName("indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner.HandRunner6Checker$Position");
        Field upField = posClass.getDeclaredField("UP");
        upField.setAccessible(true);
        IWenyanValue UP = (IWenyanValue) upField.get(null);

        Assertions.assertThrows(WenyanException.WenyanCheckerError.class, () -> checker.accept(UP));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testInvalidStepMagnitude() throws Exception {
        RandomSource random = RandomSource.create(99);
        HandRunner6Checker checker = new HandRunner6Checker(random);
        checker.init();

        // 构造非法对角线移动 (dx=1, dy=1)
        Class<?> posClass = Class.forName("indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner.HandRunner6Checker$Position");
        Constructor<?> constructor = posClass.getDeclaredConstructor(int.class, int.class);
        constructor.setAccessible(true);
        IWenyanValue invalidMove = (IWenyanValue) constructor.newInstance(1, 1);

        Assertions.assertThrows(WenyanException.WenyanCheckerError.class, () -> checker.accept(invalidMove));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }
}