package indi.wenyan.interpreter_impl.value;

import indi.wenyan.interpreter_impl.WenyanMinecraftValues;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.world.entity.Entity;

public record WenyanEntity(Entity value) implements IWenyanWarperValue<Entity>, IWenyanObject {
    public static final WenyanType<WenyanEntity> TYPE = new WenyanType<>(TypeText.Entity.string(), WenyanEntity.class);

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case WenyanSymbol.ENTITY_POS -> WenyanMinecraftValues.of(value().getPosition(0));
            case WenyanSymbol.ENTITY_MOVE -> WenyanMinecraftValues.of(value().getDeltaMovement());
            case WenyanSymbol.ENTITY_LOOK -> WenyanMinecraftValues.of(value().getLookAngle());
            case WenyanSymbol.ENTITY_ALIVE -> WenyanValues.of(value().isAlive());
            case WenyanSymbol.ENTITY_NAME -> WenyanValues.of(value().getDisplayName().getString());
            case WenyanSymbol.ENTITY_HEIGHT -> WenyanValues.of(value().getBbHeight());
            default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
        };
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
