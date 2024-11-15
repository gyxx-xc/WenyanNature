package indi.wenyan.content.checker;

import indi.wenyan.interpreter.parent.CraftingAnswerChecker;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.util.RandomSource;

public class MiningChecker extends CraftingAnswerChecker {
    public MiningChecker(RandomSource random) {
        super(random);
    }

    @Override
    protected void genInput() {
        input.add(new WenyanValue(WenyanValue.Type.INT, 100, true));
    }

    @Override
    public boolean check() {
        return ans.size() == 1 && (int) ans.getFirst().getValue() < 100;
    }
}
