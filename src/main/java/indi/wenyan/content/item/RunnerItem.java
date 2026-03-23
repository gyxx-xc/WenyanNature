package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.item.BlockItem;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerItem extends BlockItem {
    public static final String ID = "hand_runner";

    public final RunnerTier tier;

    public RunnerItem(RunnerTier tier, Properties properties) {
        super(WenyanBlocks.RUNNER_BLOCK.getBlock(tier), properties);
        this.tier = tier;
    }
}
