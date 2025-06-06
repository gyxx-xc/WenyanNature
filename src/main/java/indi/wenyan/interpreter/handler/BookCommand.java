package indi.wenyan.interpreter.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import indi.wenyan.interpreter.utils.BookUtil;
import indi.wenyan.interpreter.utils.BookLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.commands.CommandBuildContext;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent; // ← 注意：这是通用事件

import java.nio.file.Path;

import static net.neoforged.neoforge.common.NeoForge.EVENT_BUS;

/**
 * 通过 /givebook [filename] 命令，从 config/ 下的 TXT 读取并生成可写书。
 * 注意：本类需要注册到 Neoforge 的通用 Bus 上（Bus.EVENT_BUS），
 * 才能收到 RegisterCommandsEvent 事件。
 */
public class BookCommand {

    // 默认 TXT 文件名
    private static final String TXT_FILE_NAME = "wenyan_book.txt";

    /**
     * 构造函数：在这里把自己注册到 Neoforge 通用事件总线.
     * 如果你已经在 Mod 主类中注册过，也可以注释掉此处逻辑。
     */
    public BookCommand() {
    }

    /**
     * 当 Neoforge 通用总线发布 RegisterCommandsEvent 时，就会触发此方法。
     * 注意：本方法参数必须是 net.neoforged.neoforge.event.RegisterCommandsEvent
     * 而不应使用 Forge 的 RegisterCommandsEvent。
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("givebook")
                        .then(Commands.argument("filename", StringArgumentType.word())
                                .executes(ctx -> executeGiveBook(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "filename"))))
                        .executes(ctx -> executeGiveBook(ctx.getSource(), TXT_FILE_NAME))
        );
    }

    /**
     * 命令执行逻辑：读取 config/filename.txt，将其内容生成可写的书与笔发给玩家
     */
    private static int executeGiveBook(CommandSourceStack source, String filename) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendSystemMessage(Component.literal("§c只有玩家能使用此命令"));
            return Command.SINGLE_SUCCESS;
        }

        // 1. 拼出 config/filename.txt 的路径
        Path configDir = FMLPaths.CONFIGDIR.get();
        Path txtPath = configDir.resolve(filename);

        // 2. 调用 BookLoader 生成可写书（需要传 source 以获取 provider）
        ItemStack book = BookLoader.createWritableBookFromTxt(txtPath, source);

        // 3. 放入玩家背包或丢地上
        boolean added = player.getInventory().add(book);
        if (!added) {
            player.drop(book, false);
            source.sendSystemMessage(Component.literal("§e背包已满，书籍已丢在地上：" + filename));
        } else {
            source.sendSystemMessage(Component.literal("§a已将可写书发送给你：" + filename));
        }

        return Command.SINGLE_SUCCESS;
    }
}
