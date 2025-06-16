package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className SetChatHideFilterHandler
 * @Description TODO 密语
 * @date 2025/6/14 22:52
 */
public class SetChatHideFilterHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.STRING};

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        Player player=context.holder();
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String value = args.get(0).toString();
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.getCompound("wenyan_data");
        if (value == null || value.isEmpty()) {
            modData.remove("MyChatHideFilter");
        } else {
            modData.putString("MyChatHideFilter", value);
        }
        persistentData.put("wenyan_data", modData);
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
