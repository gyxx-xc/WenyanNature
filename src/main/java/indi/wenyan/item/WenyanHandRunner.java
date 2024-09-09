package indi.wenyan.item;

import indi.wenyan.gui.RunnerScreen;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static indi.wenyan.WenyanNature.LOGGER;

public class WenyanHandRunner extends Item {
    public List<String> pages;

    public WenyanHandRunner(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack>
    use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new RunnerScreen(player, itemstack, hand));
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack item, Player player) {
        if (!player.isShiftKeyDown()){
            item.get(DataComponents.WRITABLE_BOOK_CONTENT)
                    .getPages(Minecraft.getInstance().isTextFilteringEnabled())
                    .forEach(System.out::println);
            try {
                (new WenyanMainVisitor(WenyanPackages.handEnvironment(player))).run("""
                        吾有一數。曰三。名之曰「甲」。
                        為是「甲」遍。
                        \t吾有一言。曰「「問天地好在。」」。書之。
                        云云。""");
            } catch (WenyanException e) {
                player.sendSystemMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("Error").withStyle(ChatFormatting.RED));
                LOGGER.info("Error: {}", e.getMessage());
            }
            return false;
        } else {
            return super.onDroppedByPlayer(item, player);
        }
    }
}
