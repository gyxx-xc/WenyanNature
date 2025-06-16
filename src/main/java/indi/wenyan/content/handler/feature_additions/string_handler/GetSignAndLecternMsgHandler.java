package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetSignAndLecternMsgHandler
 * @Description TODO 得到告示牌或讲台信息
 * @date 2025/6/14 16:02
 */
public class GetSignAndLecternMsgHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};


    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        //获取必须参数
        Level level;
        Player player=context.holder();
        if (context.runner().runner() instanceof HandRunnerEntity runner){
            level = runner.level();
        }else{
            BlockRunner runner= (BlockRunner) context.runner().runner();
            level=runner.getLevel();
        }


        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        Double x = (Double) args.get(0), y = (Double) args.get(1), z = (Double) args.get(2);
        switch (level.getBlockEntity(BlockPos.containing(x, y, z))) {
            case SignBlockEntity sign -> {
                WenyanArrayObject wenyanArrayObject = new WenyanArrayObject();
                boolean isFrontText = sign.isFacingFrontText(player); // 判断玩家是否面向正面
                SignText signText = sign.getText(isFrontText); // 获取正面或背面的 SignText 对象
                Component[] messages = signText.getMessages(false); // false 表示不过滤客户端样式
                for (int i = 0; i < messages.length; i++) {
                    wenyanArrayObject.add(new WenyanNativeValue(
                            WenyanType.STRING,
                            messages[i].getString(),
                            false
                    ));
                }
                return new WenyanNativeValue(WenyanType.LIST,wenyanArrayObject,false);
            }
            case LecternBlockEntity lectern -> {
                WenyanArrayObject wenyanArrayObject = new WenyanArrayObject();
                ItemStack book = lectern.getBook(); // 获取讲台上的书
                // 检查是否是成书（Written Book）或可写书（Writable Book）
                if (book.is(Items.WRITTEN_BOOK)) {
                    // 获取成书内容（使用 DataComponents）
                    WrittenBookContent content = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
                    if (content != null) {
                        List<Filterable<Component>> pages = content.pages();// 所有页的文本（Component 格式）
                        for (int i = 0; i < pages.size(); i++) {
                            wenyanArrayObject.add(new WenyanNativeValue(
                                    WenyanType.STRING,
                                    pages.get(i).get(false).getString(),
                                    false
                            ));
                        }
                    }
                } else if (book.is(Items.WRITABLE_BOOK)) {
                    // 获取可写书内容
                    WritableBookContent content = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
                    if (content != null) {
                        List<Filterable<String>> pages = content.pages();// 直接是字符串列表
                        for (int i = 0; i < pages.size(); i++) {
                            wenyanArrayObject.add(new WenyanNativeValue(
                                    WenyanType.STRING,
                                    pages.get(i).get(false),
                                    false
                            ));
                        }
                    }
                }
                return new WenyanNativeValue(WenyanType.LIST,wenyanArrayObject,false);
            }
            default -> {
                throw new WenyanException(Component.translatable("error.wenyanextra.exception.no_signorlectern").getString());
            }
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
