package indi.wenyan.interpreter.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import indi.wenyan.content.item.WenyanHandRunner;
import indi.wenyan.setup.Registration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static indi.wenyan.WenyanNature.LOGGER;


public final class FileLoader {
    private FileLoader(){}

    public static DeferredItem<Item> runnerItem;

    public static final LiteralArgumentBuilder<CommandSourceStack> FILE_COMMAND = Commands.literal("wenyan")
            .then(Commands.literal("import")
                    .then(Commands.argument("filename", StringArgumentType.word())
                            // 如果只写到这里，就执行，level 默认为 0
                            .executes(ctx -> FileLoader.importFile(
                                    ctx.getSource(),
                                    StringArgumentType.getString(ctx, "filename"),
                                    0  // 默认 level
                            ))
                            // 第二个参数：level
                            .then(Commands.argument("level", IntegerArgumentType.integer())
                                    // 写了 level，就执行这个分支
                                    .executes(ctx -> FileLoader.importFile(
                                            ctx.getSource(),
                                            StringArgumentType.getString(ctx, "filename"),
                                            IntegerArgumentType.getInteger(ctx, "level")
                                    ))
                            )));

    public static ItemStack createWritableBookFromTxt(Path txtPath, CommandSourceStack source, Integer level) {
        int maxCharsPerPage = 130;
        int maxCharPerLine = 130;
        int lineCounter = 1;
        int maxLinePerPage = 12;

        LOGGER.info("[WenyanNature] 尝试读取文件，路径 = {}", txtPath.toAbsolutePath());
        source.sendSystemMessage(Component.literal("§e[WenyanNature] 尝试读取文件: " + txtPath.getFileName()));

        //Load TXT content
        String fullText;
        try {
            fullText = Files.readString(txtPath, StandardCharsets.UTF_8);
            // succeed message to player
            LOGGER.info("[WenyanNature] 成功读取文件（字符数 {}）", fullText.length());
            source.sendSystemMessage(Component.literal("§a[WenyanNature] 成功读取文件: "
                    + txtPath.getFileName() + "，共 " + fullText.length() + " 个字符"));
        } catch (IOException e) {
            // error while importing
            LOGGER.error("[WenyanNature] 无法读取文件: {}", txtPath.getFileName(), e);
            source.sendSystemMessage(Component.literal("§c[WenyanNature] 无法读取文件: " + txtPath.getFileName()));
            source.sendSystemMessage(Component.literal("§c[WenyanNature] 请检查文件是否存在或位于/config/WenyanNature/scripts目录下"));
            fullText = "§c[WenyanNature] 错误:无法读取文件 " + txtPath.getFileName();
        }
        //Separate String
        List<String> lines = Arrays.asList(fullText.split("\\r?\\n"));
        //Create empty book item

        switch (level) {
            case 1 -> runnerItem = Registration.HAND_RUNNER_1;
            case 2 -> runnerItem = Registration.HAND_RUNNER_2;
            case 3 -> runnerItem = Registration.HAND_RUNNER_3;

            default -> runnerItem = Registration.HAND_RUNNER;
        }


        WenyanHandRunner bookItem = (WenyanHandRunner) runnerItem.get();
        ItemStack handRunnerStack = new ItemStack(bookItem, 1);
        //Create pages
        List<Filterable<String>> pages = new ArrayList<>();
        StringBuilder pageBuilder = new StringBuilder();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            LOGGER.info("[WenyanNature] 第{}行: {}", i + 1, line);
            if (line.length() > maxCharPerLine || line.length() > maxCharsPerPage) {
                source.sendSystemMessage(Component.literal("§c：第" + i + 1 + "行超出95字符限制, 无法导入。请检查文件内容。"));
                return ItemStack.EMPTY;
            }

            //Exceed maxCharsPerPage or maxLinePerPage
            if (pageBuilder.length() + line.length() + 1 > maxCharsPerPage || lineCounter > maxLinePerPage) {
                // Remove change line character at the end of the page
                if (pageBuilder.length() > 0 && pageBuilder.charAt(pageBuilder.length() - 1) == '\n') {
                    pageBuilder.setLength(pageBuilder.length() - 1);
                }
                pages.add(Filterable.passThrough(pageBuilder.toString()));
                pageBuilder.setLength(0);
            }

            pageBuilder.append(line).append('\n');
            lineCounter++;
        }

        //Last Page
            if (pageBuilder.length() > 0) {
                if (pageBuilder.charAt(pageBuilder.length() - 1) == '\n') {
                    pageBuilder.setLength(pageBuilder.length() - 1);
                }
                pages.add(Filterable.passThrough(pageBuilder.toString()));
            }

        // Write into book
        WritableBookContent content = new WritableBookContent(pages);
        handRunnerStack.set(DataComponents.WRITABLE_BOOK_CONTENT, content);
        source.sendSystemMessage(Component.literal("§a[WenyanNature] 加载成功！"));
        return handRunnerStack;

    }

    public static int importFile(CommandSourceStack source, String filename, Integer level) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendSystemMessage(Component.literal("§c[WenyanNature] 只有玩家能使用此命令"));
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
        ItemStack book = createWritableBookFromTxt(txtPath, source, level);
        if (book.isEmpty()) {
            source.sendSystemMessage(Component.literal("§c[WenyanNature] 无法生成符咒，请检查文件内容或格式"));
            return Command.SINGLE_SUCCESS;
        }
        // Give to player or drop on ground
        boolean added = player.getInventory().add(book);
        if (!added) {
            player.drop(book, false);
            source.sendSystemMessage(Component.literal("§e[WenyanNature] 背包已满，符咒已丢在地上：" + filename));
        } else {
            source.sendSystemMessage(Component.literal("§a[WenyanNature] 已将符咒发送给你：" + filename));
        }

        return Command.SINGLE_SUCCESS;
    }
}
