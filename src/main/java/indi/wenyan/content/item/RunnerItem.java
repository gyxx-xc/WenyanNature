package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.GuiText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerItem extends BlockItem implements ITooltipAppendable {
    public static final String ID = "hand_runner";

    public final RunnerTier tier;

    public RunnerItem(RunnerTier tier, Properties properties) {
        super(WenyanBlocks.RUNNER_BLOCK.getBlock(tier), properties);
        this.tier = tier;
    }

    @Override
    public List<Component> getTooltip(TooltipFlag flags, ItemStack itemStack, TooltipContext context, @Nullable Player entity) {
        return List.of(GuiText.BlockRunnerTooltip.text());
    }
}
