package indi.wenyan.item;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.gui.RunnerScreen;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import indi.wenyan.setup.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static indi.wenyan.WenyanNature.LOGGER;

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
                try {
                    Semaphore semaphore = WenyanVisitor.run(WenyanPackages.handEnvironment(player), program);
                    HandRunnerEntity handRunnerEntity = new HandRunnerEntity(Registration.HAND_RUNNER_ENTITY.get(), player.level());
                    handRunnerEntity.setSemaphore(semaphore);
                    handRunnerEntity.moveTo(player.getEyePosition());
                    player.level().addFreshEntity(handRunnerEntity);
                } catch (WenyanException e) {
                    player.sendSystemMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
                } catch (Exception e) {
                    player.sendSystemMessage(Component.literal("Error").withStyle(ChatFormatting.RED));
                    LOGGER.info("Error: {}", e.getMessage());
                }
                return false;
            }
            // else : go outside
        }
        return super.onDroppedByPlayer(item, player);
    }
}
