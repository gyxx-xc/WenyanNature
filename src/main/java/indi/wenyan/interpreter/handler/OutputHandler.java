package indi.wenyan.interpreter.handler;

import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class OutputHandler extends JavacallHandler {
    private final Player player;
    public OutputHandler(Player player) {
        this.player = player;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) {
        StringBuilder result = new StringBuilder();
        for (WenyanValue arg : args) {
            result.append(result.isEmpty() ? "" : " ").append(arg.toString());
        }
        player.displayClientMessage(Component.literal(result.toString()), true);
        return null;
    }
}
