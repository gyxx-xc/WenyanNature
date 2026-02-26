package indi.wenyan.test.utils;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.network.chat.Component;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

import java.util.Arrays;
import java.util.Deque;

public class RunnerTestHelper extends ExtendedGameTestHelper {
    public RunnerTestHelper(GameTestInfo info) {
        super(info);
    }

    public void assertOutput(RunnerBlockEntity runner, String valueName, String... output) {
        if (runner.getOutputQueue().size() != output.length) {
            throw assertionException(Component.literal("Expected " + valueName + " to be " + Arrays.toString(output) +
                    ", but was " + stringFromOutputQueue(runner.getOutputQueue())));
        }
        int i = 0;
        for (Component c : runner.getOutputQueue()) {
            assertFalse(c.getStyle().getColor() != null, "has error:" + c.getString());
            if (!c.getString().equals(output[i++]))
                throw assertionException(Component.literal("Expected " + valueName + " to be " + Arrays.toString(output) +
                        ", but was " + stringFromOutputQueue(runner.getOutputQueue())));
        }
    }

    private static String stringFromOutputQueue(Deque<Component> q) {
        var sb = new StringBuilder("[");
        for (var t : q) {
            sb.append(t.getString());
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
