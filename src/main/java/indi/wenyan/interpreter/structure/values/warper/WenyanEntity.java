package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.world.entity.Entity;

public record WenyanEntity(Entity value) implements IWenyanWarperValue<Entity>, IWenyanObject {
    public static final WenyanType<WenyanEntity> TYPE = new WenyanType<>("entity", WenyanEntity.class);

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「位」" -> WenyanValues.of(value().getPosition(0));
            case "「移」" -> WenyanValues.of(value().getDeltaMovement());
            case "「向」" -> WenyanValues.of(value().getLookAngle());
            case "「活」" -> WenyanValues.of(value().isAlive());
            case "「名」" -> WenyanValues.of(value().getDisplayName().getString());
            case "「高」" -> WenyanValues.of((double) value().getBbHeight());
            default -> throw new WenyanException("实体没有这个属性: " + name);
        };
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException("not reached");
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
