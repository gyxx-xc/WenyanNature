package indi.wenyan.interpreter.handler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import indi.wenyan.interpreter.utils.FileLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent; // ← 注意：这是通用事件

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.neoforged.neoforgespi.ILaunchContext.LOGGER;


public class ImportFileHandler {

    //TXT filename
    private static final String TXT_FILE_NAME = "wenyan_book.txt";

    public ImportFileHandler() {
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("wenyan")
                    .then(Commands.literal("import")
                        // parameter <filename>
                        .then(Commands.argument("filename", StringArgumentType.word())
                            .executes(ctx -> importFile(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "filename"))))));
}


    private static int importFile(CommandSourceStack source, String filename) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendSystemMessage(Component.literal("§c只有玩家能使用此命令"));
            return Command.SINGLE_SUCCESS;
        }

        Path configDir  = FMLPaths.CONFIGDIR.get();
        Path scriptsDir = configDir.resolve("WenyanNature").resolve("scripts"); // config/WenyanNature/scripts
        // Create script directory
        try {
            Files.createDirectories(scriptsDir);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] 无法创建脚本目录: {}", scriptsDir, e);
        }

        Path txtPath = scriptsDir.resolve(filename);


        // Spawn Writable book
        ItemStack book = FileLoader.createWritableBookFromTxt(txtPath, source);
        if (book.isEmpty()) {
            source.sendSystemMessage(Component.literal("§c[WenyanNature] 无法生成书籍，请检查文件内容或格式"));
            return Command.SINGLE_SUCCESS;
        }
        // Give to player or drop on ground
        boolean added = player.getInventory().add(book);
        if (!added) {
            player.drop(book, false);
            source.sendSystemMessage(Component.literal("§e[WenyanNature] 背包已满，书籍已丢在地上：" + filename));
        } else {
            source.sendSystemMessage(Component.literal("§a[WenyanNature] 已将可写书发送给你：" + filename));
        }

        return Command.SINGLE_SUCCESS;
    }
}
