package indi.wenyan.content.item;

import indi.wenyan.setup.definitions.RunnerTier;
import lombok.Getter;
import net.minecraft.world.item.Item;

public class ThrowRunnerItem extends Item {
    public static final String ID = "throw_runner";

    @Getter
    private final RunnerTier tier;

    public ThrowRunnerItem(RunnerTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }
}
