package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

@Deprecated
public class OutputHandler implements JavacallHandler {
    public OutputHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) {
        StringBuilder result = new StringBuilder();
        for (WenyanNativeValue arg : context.args()) {
            result.append(result.isEmpty() ? "" : " ").append(arg.toString());
        }
        context.holder().displayClientMessage(Component.literal(result.toString()), true);
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
