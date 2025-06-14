package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetEntityNameHandler
 * @Description TODO 真名
 * @date 2025/6/13 14:35
 */
public class GetEntityNameHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.OBJECT};

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        if (args[0] instanceof Entity) {
            Entity entity = (Entity) args[0];
            String name;
            if (entity.getCustomName()!=null){
                name=entity.getCustomName().getString();
            }else {
                name=entity.getDisplayName().getString();
            }
            return new WenyanNativeValue(WenyanType.STRING,name,false);
        }else {
            throw new WenyanException(Component.translatable("error.wenyanextra.exception.no_entity").getString());
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
