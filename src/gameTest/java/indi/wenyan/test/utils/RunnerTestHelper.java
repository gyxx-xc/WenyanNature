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

    public void assertOutputBlock(RunnerBlockEntity runner, String valueName, String... output) {
        Deque<Component> outputQueue = runner.getOutputQueue();
        assertOutput(outputQueue, valueName, output);
    }

    public void assertOutput(Deque<Component> outputQueue, String valueName, String... output) {
        if (outputQueue.size() != output.length) {
            throw assertionException(Component.literal("Expected " + valueName + " to be size=" + output.length +
                    ", but was " + stringFromOutputQueue(outputQueue)));
        }
        int i = 0;
        for (Component c : outputQueue) {
            assertFalse(c.getStyle().getColor() != null, "has error:" + c.getString());
            if (!c.getString().equals(output[i++]))
                throw assertionException(Component.literal("Expected " + valueName + " to be " + Arrays.toString(output) +
                        ", but was " + stringFromOutputQueue(outputQueue)));
        }
    }

    public void assertErrorDisplay(Deque<Component> outputQueue, String string) {
        if (outputQueue.isEmpty())
            throw assertionException(Component.literal("empty output"));
        Component last = outputQueue.getLast();
        assertTrue(last.getStyle().getColor() != null, "no error:" + last.getString());
        if (!last.getString().contains(string))
            throw assertionException(Component.literal("Expected error message to contain " + string +
                    ", but was " + stringFromOutputQueue(outputQueue)));
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
