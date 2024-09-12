package indi.wenyan.item;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.gui.RunnerScreen;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WenyanHandRunner extends Item {
    public WenyanHandRunner(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack>
    use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide())
            Minecraft.getInstance().setScreen(new RunnerScreen(player, itemstack, hand));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack item, Player player) {
        if (!player.isShiftKeyDown()){
            WritableBookContent writableBookContent = item.get(DataComponents.WRITABLE_BOOK_CONTENT);
            if (writableBookContent != null) {
                Stream<String> pages = writableBookContent.getPages(Minecraft.getInstance().isTextFilteringEnabled());
                String program = pages.collect(Collectors.joining("\n"));
                HandRunnerEntity handRunnerEntity = new HandRunnerEntity(Registration.HAND_RUNNER_ENTITY.get(), player.level());
                handRunnerEntity.moveTo(player.getEyePosition());
                handRunnerEntity.code = program;
                handRunnerEntity.holder = player;
                player.level().addFreshEntity(handRunnerEntity);

                item.shrink(1);
                return false;
            }
            // else : go outside
        }
        return super.onDroppedByPlayer(item, player);
    }
}
