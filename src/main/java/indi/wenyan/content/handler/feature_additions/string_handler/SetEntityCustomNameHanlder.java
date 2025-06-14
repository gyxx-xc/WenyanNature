package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className SetEntityCustomNameHanlder
 * @Description TODO
 * @date 2025/6/13 19:06
 */
public class SetEntityCustomNameHanlder implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.OBJECT,WenyanType.STRING};

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        if (args[0] instanceof Entity) {
            Entity entity = (Entity) args[0];
            if (entity instanceof Player) {
                return WenyanValue.NULL;
            } else if (entity instanceof ItemEntity item) {
                item.getItem().set(DataComponents.CUSTOM_NAME, Component.literal(args[1].toString()));
            }else {
                entity.setCustomName(Component.literal(args[1].toString()));
            }
        }
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}

