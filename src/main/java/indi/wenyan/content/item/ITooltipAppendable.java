package indi.wenyan.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ITooltipAppendable {
    List<Component> getTooltip(TooltipFlag flags, ItemStack itemStack, Item.TooltipContext context, @Nullable Player entity);
}
