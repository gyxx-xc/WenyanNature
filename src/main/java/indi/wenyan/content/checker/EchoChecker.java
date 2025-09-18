package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.util.RandomSource;

/**
 * EchoChecker is a simple checker that verifies if the input matches a randomly generated integer.
 * It initializes a random integer as the answer and checks if the input matches this value.
 * <p>
 * output var0
 */
public class EchoChecker extends CraftingAnswerChecker {
    private IWenyanValue ans;

    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);
        ans = WenyanValues.of(random.nextInt());
        setVariable(0, ans);
    }

    @Override
    public void accept(IWenyanValue value) {
        try {
            if (IWenyanValue.equals(value, ans)){
                setStatus(Result.ANSWER_CORRECT);
                return;
            }
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
        setStatus(Result.WRONG_ANSWER);
    }
}
