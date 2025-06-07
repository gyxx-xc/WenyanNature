package indi.wenyan.interpreter.utils;

import indi.wenyan.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.world.item.component.WritableBookContent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;

import  indi.wenyan.content.item.WenyanHandRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * 工具类：把外部 TXT 内容读入并生成一本“可编辑的书与笔”（Writable Book）。
 * 适用于 NeoForge 1.20.1 (Forge 47.3.x 系列)。
 */
public class BookLoader {

    // 用于在控制台或日志中打印调试信息
    private static final Logger LOGGER = LogManager.getLogger("BookLoader");
    public static final DeferredItem<Item> HAND_RUNNER= Registration.HAND_RUNNER;
    /**
     * 从指定的 .txt 文件路径读取内容，拆行后生成一本“未签名、可写”的书与笔（Writable Book）。
     *
     * @param txtPath 外部 txt 文件的路径（绝对或相对），例如 run/config/wenyan_book.txt
     * @param source  当前命令上下文，用于获取 HolderLookup.Provider 并给玩家发消息
     * @return 可写书的 ItemStack，如果反序列化失败则返回 ItemStack.EMPTY
     */
    public static ItemStack createWritableBookFromTxt(Path txtPath, CommandSourceStack source) {
        // —— 1. 在日志里和聊天框里打印“要读取的文件路径” ——
        LOGGER.info("[BookLoader] 尝试读取文件，路径 = {}", txtPath.toAbsolutePath());
        source.sendSystemMessage(Component.literal("§e[BookLoader] 尝试读取文件: " + txtPath.getFileName()));

        // —— 2. 读取整个 TXT 文件内容 ——
        String fullText;
        try {
            fullText = Files.readString(txtPath, StandardCharsets.UTF_8);
            // 成功读取后打印日志和给玩家提示
            LOGGER.info("[BookLoader] 成功读取文件（字符数 {}）", fullText.length());
            source.sendSystemMessage(Component.literal("§a[BookLoader] 成功读取文件: "
                    + txtPath.getFileName() + "，共 " + fullText.length() + " 个字符"));
        } catch (IOException e) {
            // 读取失败时，打印异常堆栈并通知玩家
            LOGGER.error("[BookLoader] 无法读取文件: {}", txtPath.getFileName(), e);
            source.sendSystemMessage(Component.literal("§c[BookLoader] 无法读取文件: " + txtPath.getFileName()));
            source.sendSystemMessage(Component.literal("§c[BookLoader] 请检查文件是否存在或位于/config/WenyanNature/scripts目录下"));
            // 用一条错误提示作为“唯一一页”文字
            fullText = "§c[错误] 无法读取文件: " + txtPath.getFileName();
        }

        // —— 3. 按行拆分文本 ——
        // 这里我们举例：按换行符拆
        // 3. 按行拆分文本
        List<String> lines = Arrays.asList(fullText.split("\\r?\\n"));

// 4. 创建一个空白“可写书”
        WenyanHandRunner bookItem = (WenyanHandRunner) HAND_RUNNER.get();
        ItemStack handRunnerStack = new ItemStack(bookItem, 1);

// 5. 按页累积，254 字符为阈值（可按需调整）
        int maxCharsPerPage = 130;
        int maxCharPerLine = 130;
        int lineCounter = 1;
        int maxLinePerPage = 12;
        List<Filterable<String>> pages = new ArrayList<>();
        StringBuilder pageBuilder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            LOGGER.info("[BookLoader] 第{}行: {}", i + 1, line);
            if (line.length() > maxCharPerLine) {
                source.sendSystemMessage(Component.literal("§c：第" + i + 1 + "行超出95字符限制, 无法导入。请检查文件内容。"));
                return ItemStack.EMPTY;
            }
            source.sendSystemMessage(
                    Component.literal("§c[BookLoader] 第" + (i + 1) + "行: " + line)
            );

            // 如果加入本行后超过阈值，就先“翻页”
            if (pageBuilder.length() + line.length() + 1 > maxCharsPerPage || lineCounter > maxLinePerPage) {
                // 去掉末尾可能多余的换行符
                if (pageBuilder.length() > 0 && pageBuilder.charAt(pageBuilder.length() - 1) == '\n') {
                    pageBuilder.setLength(pageBuilder.length() - 1);
                }
                pages.add(Filterable.passThrough(pageBuilder.toString()));
                pageBuilder.setLength(0);
            }

            // 把本行累积到当前页，并添加换行
            pageBuilder.append(line).append('\n');
            lineCounter++;
        }

// 最后一页（如果还有内容）
            if (pageBuilder.length() > 0) {
                if (pageBuilder.charAt(pageBuilder.length() - 1) == '\n') {
                    pageBuilder.setLength(pageBuilder.length() - 1);
                }
                pages.add(Filterable.passThrough(pageBuilder.toString()));
            }

// 写入可写书内容
        WritableBookContent content = new WritableBookContent(pages);
        handRunnerStack.set(DataComponents.WRITABLE_BOOK_CONTENT, content);

        return handRunnerStack;

    }
}
