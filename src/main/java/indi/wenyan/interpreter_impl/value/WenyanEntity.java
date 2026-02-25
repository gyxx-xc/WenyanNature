package indi.wenyan.interpreter_impl.value;

import indi.wenyan.interpreter_impl.WenyanMinecraftValues;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.world.entity.Entity;

public record WenyanEntity(Entity value) implements IWenyanWarperValue<Entity>, IWenyanObject {
    public static final WenyanType<WenyanEntity> TYPE = new WenyanType<>("entity", WenyanEntity.class);

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case "「位」" -> WenyanMinecraftValues.of(value().getPosition(0));
            case "「移」" -> WenyanMinecraftValues.of(value().getDeltaMovement());
            case "「向」" -> WenyanMinecraftValues.of(value().getLookAngle());
            case "「活」" -> WenyanValues.of(value().isAlive());
            case "「名」" -> WenyanValues.of(value().getDisplayName().getString());
            case "「高」" -> WenyanValues.of(value().getBbHeight());
            default -> throw new WenyanException("实体没有这个属性: " + name);
        };
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
