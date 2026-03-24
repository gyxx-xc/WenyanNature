package indi.wenyan.content.checker.checker.handrunner;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

import static java.lang.Math.sqrt;

public class HandRunner3Checker extends ValueAnswerChecker {
    public HandRunner3Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        int n = random.nextInt(1000000) + 1;
        setVariable(0,WenyanValues.of(n));

        if (n <= 1) {
            ans = WenyanValues.of(false);
            return;
        }

        for (int i=2; i<=(int)sqrt(n); i++)
        {
            if (n%i == 0){
                ans = WenyanValues.of(false);
                return;
            }
        }
        ans = WenyanValues.of(true);
    }

}
