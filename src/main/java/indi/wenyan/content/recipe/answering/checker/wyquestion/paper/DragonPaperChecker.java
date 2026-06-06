package indi.wenyan.content.recipe.answering.checker.wyquestion.paper;

import indi.wenyan.content.recipe.answering.checker.ValueAnswerChecker;
import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanObject;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DragonPaperChecker extends ValueAnswerChecker {
    public DragonPaperChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();

        // 1. 生成满足条件的 n 和 m
        // 保证 m >= n - 1，以确保图可以连通
        int n = random.nextInt(1000000) + 1;
        int m = n == 1 ? random.nextInt(1000000) + 1 : (n - 1) + random.nextInt(1000000 - n + 2);

        // 记录输入变量（根据你的框架需求调整，这里举例暴露 n 和 m）
        setVariable(0, WenyanValues.of(n));
        setVariable(1, WenyanValues.of(m));

        // 2. 初始化链式前向星所需数组 (采用局部变量利于GC释放)
        int[] head1 = new int[n + 1], head2 = new int[n + 1];
        int[] to1 = new int[m + 1], to2 = new int[m + 1];
        int[] next1 = new int[m + 1], next2 = new int[m + 1];
        int[] weight1 = new int[m + 1], weight2 = new int[m + 1];
        int num1 = 0, num2 = 0;

        List<IWenyanValue> edges = new ArrayList<>();

        // 3. 生成随机连通图
        // 步骤 A: 生成一棵以 1 为根的树，保证从 1 出发绝对可达所有点
        // FIXME: need ensure reverse graph's reachability
        for (int i = 2; i <= n; i++) {
            int u = random.nextInt(i - 1) + 1; // 从已连通的节点集合 [1, i-1] 中随机取点
            int w = random.nextInt(1000000000) + 1;

            to1[++num1] = i;
            weight1[num1] = w;
            next1[num1] = head1[u];
            head1[u] = num1;
            to2[++num2] = u;
            weight2[num2] = w;
            next2[num2] = head2[i];
            head2[i] = num2;

            edges.add(new Edge(i, u, w));
        }

        // 步骤 B: 随机生成剩余的 m - (n - 1) 条边
        for (int i = n; i <= m; i++) {
            int u = random.nextInt(n) + 1;
            int v = random.nextInt(n) + 1;
            int w = random.nextInt(1000000000) + 1;

            to1[++num1] = v;
            weight1[num1] = w;
            next1[num1] = head1[u];
            head1[u] = num1;
            to2[++num2] = u;
            weight2[num2] = w;
            next2[num2] = head2[v];
            head2[v] = num2;

            edges.add(new Edge(v, u, w));
        }

        setVariable(2, WenyanValues.of(edges));

        // 4. 计算运算逻辑
        long[] dis = new long[n + 1];
        boolean[] inq = new boolean[n + 1];
        int[] queue = new int[n + 5]; // 原生数组实现的环形队列，速度最快
        long totalCost = 0;

        // --- 执行正向 SPFA (去程) ---
        Arrays.fill(dis, Long.MAX_VALUE);
        dis[1] = 0;
        int head = 0, tail = 0;
        queue[tail++] = 1;
        inq[1] = true;

        while (head != tail) {
            int u = queue[head++];
            if (head == queue.length) head = 0;
            inq[u] = false;

            for (int k = head1[u]; k != 0; k = next1[k]) {
                int v = to1[k];
                int w = weight1[k];
                if (dis[u] + w < dis[v]) {
                    dis[v] = dis[u] + w;
                    if (!inq[v]) {
                        queue[tail++] = v;
                        if (tail == queue.length) tail = 0;
                        inq[v] = true;
                    }
                }
            }
        }
        for (int i = 2; i <= n; i++) if (dis[i] != Long.MAX_VALUE) totalCost += dis[i];

        // --- 执行反向 SPFA (回程) ---
        Arrays.fill(dis, Long.MAX_VALUE);
        dis[1] = 0;
        Arrays.fill(inq, false);
        head = 0;
        tail = 0;
        queue[tail++] = 1;
        inq[1] = true;

        while (head != tail) {
            int u = queue[head++];
            if (head == queue.length) head = 0;
            inq[u] = false;

            for (int k = head2[u]; k != 0; k = next2[k]) {
                int v = to2[k];
                int w = weight2[k];
                if (dis[u] + w < dis[v]) {
                    dis[v] = dis[u] + w;
                    if (!inq[v]) {
                        queue[tail++] = v;
                        if (tail == queue.length) tail = 0;
                        inq[v] = true;
                    }
                }
            }
        }
        for (int i = 2; i <= n; i++) if (dis[i] != Long.MAX_VALUE) totalCost += dis[i];

        // 5. 最终输出结果
        ans = WenyanValues.of(totalCost);
    }


    private record Edge(int from, int to, int distance) implements IWenyanObject {
        public static final WenyanType<Edge> TYPE =
                new WenyanType<>(TypeText.Edge.string(), Edge.class);

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        @Override
        public IWenyanValue getAttribute(String name) throws WenyanException {
            return switch (name) {
                case "「起」" -> WenyanValues.of(from);
                case "「止」" -> WenyanValues.of(to);
                case "「距」" -> WenyanValues.of(distance);
                default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
            };
        }
    }
}

