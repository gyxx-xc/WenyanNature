package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.util.RandomSource;

/**
 * PlusChecker is a simple checker that verifies if the input matches the sum of two randomly generated integers.
 * It initializes two random integers as inputs and checks if the input matches their sum.
 * <p>
 * output var0 + var1
 */
public class PlusChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public PlusChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        setVariable(0, new WenyanInteger(a));
        setVariable(1, new WenyanInteger(b));
        ans = new WenyanInteger(a + b);
    }

    @Override
    public void accept(IWenyanValue value) {
        try {
            if (IWenyanValue.equals(value, ans)) {
                setStatus(Result.ANSWER_CORRECT);
            } else {
                setStatus(Result.WRONG_ANSWER);
            }
        } catch (Exception e) {
            setStatus(Result.RUNTIME_ERROR);
        }
    }
}
