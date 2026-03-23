package indi.wenyan.interpreter_impl.value;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.world.entity.player.Player;

public record WenyanPlayer(WenyanEntity valueWarper)
        implements IWenyanWarperValue<Player>, IWenyanObject {
    public static final WenyanType<WenyanPlayer> TYPE = new WenyanType<>(TypeText.Player.string(), WenyanPlayer.class);
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_UUID = "uuid";

    public WenyanPlayer(Player valueWarper) {
        this(new WenyanEntity(valueWarper));
    }

    public IWenyanValue getAttribute(String name) throws WenyanException {
        try {
            return valueWarper.getAttribute(name);
        } catch (WenyanException e) {
            return switch (name) {
                case PLAYER_NAME -> WenyanValues.of(value().getName().getString());
                case PLAYER_UUID -> WenyanValues.of(value().getUUID().toString());
                default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
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
