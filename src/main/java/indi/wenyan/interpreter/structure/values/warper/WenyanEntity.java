package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import net.minecraft.world.entity.Entity;

public record WenyanEntity(Entity value) implements IWenyanWarperValue<Entity>, IWenyanObject {
    public static final WenyanType<WenyanEntity> TYPE = new WenyanType<>("entity", WenyanEntity.class);

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「位」" -> new WenyanVec3(value().getPosition(0));
            case "「移」" -> new WenyanVec3(value().getDeltaMovement());
            case "「向」" -> new WenyanVec3(value().getLookAngle());
            case "「活」" -> new WenyanBoolean(value().isAlive());
            case "「名」" -> new WenyanString(value().getDisplayName().getString());
            case "「高」" -> new WenyanDouble((double) value().getBbHeight());
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
