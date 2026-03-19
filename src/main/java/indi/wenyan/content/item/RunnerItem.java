package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.item.BlockItem;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerItem extends BlockItem {
    // String constants for registry names and entity IDs
    public static final String ID_0 = "hand_runner_0";
    public static final String ID_1 = "hand_runner_1";
    public static final String ID_2 = "hand_runner_2";
    public static final String ID_3 = "hand_runner_3";
    public static final String ID_4 = "hand_runner_4";
    public static final String ID_5 = "hand_runner_5";
    public static final String ID_6 = "hand_runner_6";

    public final int runningLevel;

    public RunnerItem(Properties properties, int runningLevel) {
        super(switch (runningLevel) {
            case 0 -> WenyanBlocks.RUNNER_BLOCK_0.get();
            case 1 -> WenyanBlocks.RUNNER_BLOCK_1.get();
            case 2 -> WenyanBlocks.RUNNER_BLOCK_2.get();
            case 3 -> WenyanBlocks.RUNNER_BLOCK_3.get();
            case 4 -> WenyanBlocks.RUNNER_BLOCK_4.get();
            case 5 -> WenyanBlocks.RUNNER_BLOCK_5.get();
            case 6 -> WenyanBlocks.RUNNER_BLOCK_6.get();
            default -> throw new IllegalArgumentException("Invalid running level: " + runningLevel);
        }, properties);
        this.runningLevel = runningLevel;
    }
}
