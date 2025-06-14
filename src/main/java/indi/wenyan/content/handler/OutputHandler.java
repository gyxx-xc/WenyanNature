package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

@Deprecated
public class OutputHandler implements JavacallHandler {
    private final Player player;
    public OutputHandler(Player player) {
        this.player = player;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) {
        StringBuilder result = new StringBuilder();
        for (WenyanNativeValue arg : args) {
            result.append(result.isEmpty() ? "" : " ").append(arg.toString());
        }
        player.displayClientMessage(Component.literal(result.toString()), true);
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
