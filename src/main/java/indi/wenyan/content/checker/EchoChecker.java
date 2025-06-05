package indi.wenyan.content.checker;

import indi.wenyan.interpreter.utils.CraftingAnswerChecker;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.util.RandomSource;

public class EchoChecker extends CraftingAnswerChecker {
    public EchoChecker(RandomSource random) {
        super(random);
    }

    @Override
    protected void genInput() {
        input.add(new WenyanValue(WenyanValue.Type.INT, random.nextInt(), true));
        input.add(new WenyanValue(WenyanValue.Type.DOUBLE, random.nextDouble(), true));
        input.add(new WenyanValue(WenyanValue.Type.STRING, Integer.toString(random.nextInt()), true));
    }

    @Override
    public boolean check() {
        try {
            if (ans.size() != input.size()) return false;
            for (int i = 0; i < input.size(); i++) {
                if (!ans.get(i).equals(input.get(i)))
                    return false;
            }
        } catch (WenyanException.WenyanTypeException e) {
            return false;
        }
        return true;
    }
}
