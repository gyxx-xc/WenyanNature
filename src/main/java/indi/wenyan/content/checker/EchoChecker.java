package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.util.RandomSource;

/**
 * EchoChecker is a simple checker that verifies if the input matches a randomly generated integer.
 * It initializes a random integer as the answer and checks if the input matches this value.
 * <p>
 * output var0
 */
public class EchoChecker extends CraftingAnswerChecker {
    private WenyanNativeValue ans;

    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    public void init(WenyanProgram program) {
        super.init(program);
        ans = new WenyanNativeValue(WenyanType.INT, random.nextInt(), true);
        setVariable(0, ans);
    }

    @Override
    public void accept(WenyanNativeValue value) {
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
