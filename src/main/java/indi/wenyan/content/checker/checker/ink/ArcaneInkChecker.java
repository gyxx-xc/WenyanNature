package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class ArcaneInkChecker extends CraftingAnswerChecker {
    public ArcaneInkChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int n = random.nextInt(19) + 1;
        setVariable(0,WenyanValues.of(n));

        int[] a = new int[2000];
        a[1] = 1;
        int len = 1;

        for (int i = 0; i < n + 1; i++) {
            int carry = 0;
            for (int j = 1; j <= len; j++) {
                a[j] = a[j] * 2 + carry;
                carry = a[j] / 10;
                a[j] %= 10;
            }
            while (carry > 0) {
                len++;
                a[len] = carry % 10;
                carry /= 10;
            }
        }

        // 个位减 2
        a[1] -= 2;

        // 倒序拼接输出结果
        StringBuilder output = new StringBuilder();
        for (int i = len; i > 0; i--) {
            output.append(a[i]);
        }

        ans = WenyanValues.of(output.toString());
    }
}