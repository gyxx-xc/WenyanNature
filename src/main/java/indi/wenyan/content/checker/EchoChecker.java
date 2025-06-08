package indi.wenyan.content.checker;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.util.RandomSource;

public class EchoChecker extends CraftingAnswerChecker {
    private WenyanValue ans;

    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    protected void genInput() {
        ans = new WenyanValue(WenyanValue.Type.INT, random.nextInt(), true);
        input.clear();
        input.add(ans);
    }

    @Override
    public void accept(WenyanValue value) {
        try {
            if (value.equals(ans)){
                setStatus(Result.ANSWER_CORRECT);
                return;
            }
        } catch (WenyanException.WenyanTypeException e) {
            setStatus(Result.RUNTIME_ERROR);
        }
        setStatus(Result.WRONG_ANSWER);
    }
}
