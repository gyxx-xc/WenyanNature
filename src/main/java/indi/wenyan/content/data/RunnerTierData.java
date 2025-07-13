package indi.wenyan.content.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public record RunnerTierData(int tier) implements TooltipProvider {
    public static final Codec<RunnerTierData> CODEC = Codec.INT.xmap(
            RunnerTierData::new, RunnerTierData::tier
    );
    public static final String ID = "runner_tier_data";

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RunnerTierData that = (RunnerTierData) obj;
        return tier == that.tier;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tier);
    }
}
