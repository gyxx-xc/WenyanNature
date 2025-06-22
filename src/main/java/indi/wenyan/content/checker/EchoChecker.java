package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanInteger;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import net.minecraft.util.RandomSource;

/**
 * EchoChecker is a simple checker that verifies if the input matches a randomly generated integer.
 * It initializes a random integer as the answer and checks if the input matches this value.
 * <p>
 * output var0
 */
public class EchoChecker extends CraftingAnswerChecker {
    private WenyanValue ans;

    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);
        ans = new WenyanInteger(random.nextInt());
        setVariable(0, ans);
    }

    @Override
    public void accept(WenyanValue value) {
        try {
            if (WenyanValue.equals(value, ans)){
                setStatus(Result.ANSWER_CORRECT);
                return;
            }
        } catch (WenyanException.WenyanTypeException e) {
            setStatus(Result.RUNTIME_ERROR);
        }
        setStatus(Result.WRONG_ANSWER);
    }
}
