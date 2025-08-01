package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public record WenyanCapabilitySlot(IItemHandler capabilities, int slot) implements IWenyanObject {
    public static final WenyanType<WenyanCapabilitySlot> TYPE = new WenyanType<>("item_slot",
            WenyanCapabilitySlot.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        return (new WenyanInteger(slot)).toString();
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return null;
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {

    }
}
