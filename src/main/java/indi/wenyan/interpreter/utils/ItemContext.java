package indi.wenyan.interpreter.utils;

import lombok.Value;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Value
@Accessors(fluent = true)
public class ItemContext implements IHandleContext{
    ItemStack itemStack;
    Level level;
    Player player;
    int slotId;
    boolean isSelected;
}
