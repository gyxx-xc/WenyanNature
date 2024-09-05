package indi.wenyan.item;

import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.visitor.WenyanExprVisitor;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static indi.wenyan.WenyanNature.LOGGER;

public class WenyanHandRunner extends Item {
    public WenyanHandRunner(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack>
    use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new BookEditScreen(player, itemstack, hand));
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (!player.isShiftKeyDown()){
            try {
                (new WenyanMainVisitor()).visit(
                        new WenyanRParser(
                                new CommonTokenStream(
                                        new WenyanRLexer(CharStreams.fromString("書「「問天地好在。」」。"))))
                                .program());
            } catch (WenyanException e) {
                LOGGER.info("Error: {}", e.getMessage());
            }
            return false;
        } else {
            return super.onDroppedByPlayer(item, player);
        }
    }
}
