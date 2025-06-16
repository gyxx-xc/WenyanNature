package indi.wenyan.content.handler.feature_additions.magic_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className TeleportHandler
 * @Description TODO 术法 - 实体传送
 * @date 2025/6/5 22:29
 */
public class TeleportHandler implements JavacallHandler {
    private final Level level;
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.OBJECT,WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};
    public TeleportHandler(Level level) {
        this.level = level;
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        Object object = args.get(0);
        if (object instanceof Entity entity) {
            double x = (double) args.get(1);
            double y = (double) args.get(2);
            double z = (double) args.get(3);
            entity.teleportTo(x, y, z);
        }else {
            throw new WenyanException(Component.translatable("error.wenyanextra.exception.no_entity").getString());
        }
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
