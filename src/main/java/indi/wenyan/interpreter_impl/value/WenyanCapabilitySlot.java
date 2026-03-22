package indi.wenyan.interpreter_impl.value;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an item slot with position and capability information.
 * Used for interacting with inventory slots in the Minecraft environment.
 */
public record WenyanCapabilitySlot(Vec3 pose, ResourceHandler<ItemResource> capabilities, int slot) implements IWenyanObject {
    public static final WenyanType<WenyanCapabilitySlot> TYPE = new WenyanType<>(TypeText.Itemslot.string(),
            WenyanCapabilitySlot.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    public ItemStack getStack() {
        return ItemUtil.getStack(capabilities, slot);
    }

    @Override
    public @NotNull String toString() {
        return getStack().getDisplayName().getString();
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WenyanCapabilitySlot slot1)) return false;

        return slot() == slot1.slot() && Objects.equals(capabilities(), slot1.capabilities());
    }
}
