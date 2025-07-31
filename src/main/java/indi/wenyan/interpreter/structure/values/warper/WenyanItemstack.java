package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record WenyanItemstack(ItemStack value)
        implements IWenyanWarperValue<ItemStack>{
    public static final WenyanType<WenyanItemstack> TYPE = new WenyanType<>("itemstack",
            WenyanItemstack.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        return value.getHoverName().getString();
    }
}
