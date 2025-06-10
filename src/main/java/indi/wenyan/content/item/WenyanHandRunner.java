package indi.wenyan.content.item;

import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.gui.HandRunnerScreen;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WenyanHandRunner extends BlockItem {
    public final int runningLevel;

    public WenyanHandRunner(Properties properties, int runningLevel) {
        super(Registration.RUNNER_BLOCK.get(), properties);
        this.runningLevel = runningLevel;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull InteractionResultHolder<ItemStack>
    use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide())
            Minecraft.getInstance().setScreen(new HandRunnerScreen(player, itemstack, hand));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack item, Player player) {
        if (!player.isShiftKeyDown()){
            WritableBookContent writableBookContent = item.get(DataComponents.WRITABLE_BOOK_CONTENT);
            if (writableBookContent != null) {
                Stream<String> pages = writableBookContent.getPages(false);
                String program = pages.collect(Collectors.joining("\n"));
                HandRunnerEntity handRunnerEntity = new HandRunnerEntity(player, program, runningLevel);
                player.level().addFreshEntity(handRunnerEntity);

                item.shrink(1);
                return false;
            } // else : go outside
        }
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (Objects.requireNonNull(context.getPlayer()).isShiftKeyDown()) {
            context.getItemInHand().set(Registration.TIER_DATA.get(),
                    new RunnerTierData(runningLevel));
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }
}
