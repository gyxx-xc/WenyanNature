package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import indi.wenyan.interpreter.utils.WenyanDataParser;
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
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className SetSignAndLecternMsgHandler
 * @Description TODO
 * @date 2025/6/14 16:58
 */
public class SetSignAndLecternMsgHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.LIST,WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};


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
        WenyanArrayObject list= (WenyanArrayObject) args.get(0);
        // 获取列表长度
        int length = (int) list.getAttribute(WenyanDataParser.LONG_ID).getValue();
        for (int i = 1; i <= length; i++) {
            WenyanNativeValue obj = list.get(new WenyanNativeValue(WenyanType.INT, i,false));
            if (obj.type()!=WenyanType.STRING){
                throw new WenyanException("謬：列应全为文字！然，其中掺杂【"+obj+"】");
            }
        }
        Double x = (Double) args.get(1), y = (Double) args.get(2), z = (Double) args.get(3);
        switch (level.getBlockEntity(BlockPos.containing(x, y, z))) {
            //告示牌
            case SignBlockEntity sign -> {
                if (sign.isWaxed()){
                    break;
                }
                if (sign.getPlayerWhoMayEdit()!=null){
                    if (!player.getUUID().equals(sign.getPlayerWhoMayEdit())){
                        break;
                    }
                }
                SignText newText = new SignText();
                for (int i = 1; i <= Math.min(length, 4); i++) { // 最多4行
                    String text = String.valueOf(list.get(new WenyanNativeValue(WenyanType.INT, i, false)).getValue());
                    newText=newText.setMessage(i - 1, Component.literal(text));
                }
                boolean isFrontText = sign.isFacingFrontText(player); // 判断玩家是否面向正面
                sign.setText(newText, isFrontText);
                sign.setChanged();
                //同步到客户端（仅在服务端执行）
                if (!level.isClientSide) {
                    BlockState currentState = level.getBlockState(BlockPos.containing(x, y, z));
                    level.sendBlockUpdated(BlockPos.containing(x, y, z), currentState, currentState, 3); // 强制同步
                }
            }
            //讲台
            case LecternBlockEntity lectern -> {
                WenyanArrayObject wenyanArrayObject = new WenyanArrayObject();
                ItemStack book = lectern.getBook(); // 获取讲台上的书

                if (book.is(Items.WRITTEN_BOOK)) {
                    // 获取现有内容（若为空则使用默认值）
                    WrittenBookContent oldContent = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
                    if (oldContent == null) {
                        oldContent = WrittenBookContent.EMPTY; // 使用默认空内容
                    }
                    // 准备新页面（转换为 Filterable<Component>）
                    List<Filterable<Component>> newPages = new ArrayList<>();
                    for (int i = 1; i <= length; i++) {
                        String text = String.valueOf(list.get(new WenyanNativeValue(WenyanType.INT, i, false)).getValue());
                        newPages.add(Filterable.passThrough(Component.literal(text)));
                    }
                    // 创建新内容（保留原有标题、作者、世代）
                    WrittenBookContent newContent = new WrittenBookContent(
                            oldContent.title(),      // 保留原标题
                            oldContent.author(),     // 保留原作者
                            oldContent.generation(), // 保留原世代
                            newPages,                // 新页面
                            oldContent.resolved()    // 保留解析状态
                    );
                    // 更新书本并同步
                    book.set(DataComponents.WRITTEN_BOOK_CONTENT, newContent);
                    lectern.setBook(book);
                    lectern.setChanged();
                    if (!lectern.getLevel().isClientSide) {
                        lectern.getLevel().sendBlockUpdated(
                                lectern.getBlockPos(),
                                lectern.getBlockState(),
                                lectern.getBlockState(),
                                3
                        );
                    }
                } else if (book.is(Items.WRITABLE_BOOK)) {
                    // 1. 准备新页面内容（每页一个 Filterable<String>）
                    List<Filterable<String>> newPages = new ArrayList<>();
                    for (int i = 1; i <= length; i++) {
                        String text = String.valueOf(list.get(new WenyanNativeValue(WenyanType.INT, i, false)).getValue());
                        newPages.add(Filterable.passThrough(text));
                    }
                    // 2. 创建新内容并替换
                    book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(newPages));
                }
                // 3. 更新讲台并同步
                lectern.setBook(book);
                lectern.setChanged();
                if (!lectern.getLevel().isClientSide) {
                    lectern.getLevel().sendBlockUpdated(
                            lectern.getBlockPos(),
                            lectern.getBlockState(),
                            lectern.getBlockState(),
                            3
                    );
                }
            }
            default -> {
                return WenyanValue.NULL;
            }
        }
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

}
