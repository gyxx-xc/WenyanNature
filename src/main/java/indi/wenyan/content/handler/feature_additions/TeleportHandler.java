package indi.wenyan.content.handler.feature_additions;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        Object object = args[0];
        Entity entity;
        if (object instanceof Entity) {
            entity = (Entity) object;
            double x = Double.valueOf(String.valueOf(args[1]));
            double y = Double.valueOf(String.valueOf(args[2]));
            double z = Double.valueOf(String.valueOf(args[3]));
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
