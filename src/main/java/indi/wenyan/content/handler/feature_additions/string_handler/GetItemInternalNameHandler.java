package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetItemInternalNameHandler
 * @Description TODO 物之隐名
 * @date 2025/6/13 14:35
 */
public class GetItemInternalNameHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.OBJECT};

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        if (args.get(0) instanceof ItemEntity item) {
            return new WenyanNativeValue(WenyanType.STRING,
                    String.valueOf(item.getItem().getItem()),
                    false);
        }else{
            throw new WenyanException(Component.translatable("error.wenyanextra.exception.no_itementity").getString());
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
