package indi.wenyan.content.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/// Block item for the creative power block (創核石): renders with the
/// enchantment glint to distinguish it from the regular power block (算核).
public class CreativePowerBlockItem extends BlockItem {
    public CreativePowerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
