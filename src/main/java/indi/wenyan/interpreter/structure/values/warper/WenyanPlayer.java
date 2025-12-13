package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.world.entity.player.Player;

public record WenyanPlayer(WenyanEntity valueWarper)
        implements IWenyanWarperValue<Player>, IWenyanObject {
    public static final WenyanType<WenyanPlayer> TYPE = new WenyanType<>("player", WenyanPlayer.class);

    public WenyanPlayer(Player valueWarper) {
        this(new WenyanEntity(valueWarper));
    }

    public IWenyanValue getAttribute(String name) {
        try {
            return valueWarper.getAttribute(name);
        } catch (WenyanException e) {
            return switch (name) {
                case "name" -> WenyanValues.of(value().getName().getString());
                case "uuid" -> WenyanValues.of(value().getUUID().toString());
                default -> throw new WenyanException("玩家没有这个属性: " + name);
            };
        }
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public Player value() {
        return (Player) valueWarper.value();
    }
}
