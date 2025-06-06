package indi.wenyan.interpreter.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.commands.CommandSourceStack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 工具类：把外部 TXT 内容读入并生成一本“可编辑的书与笔”（Writable Book）。
 * 适用于 NeoForge 1.20.1 (Forge 47.3.x 系列)。
 */
public class BookLoader {

    /**
     * 从指定的 .txt 文件路径读取内容，拆成多页后生成一本“未签名、可编辑”的书与笔（Writable Book）。
     *
     * @param txtPath 外部 txt 文件的路径（可以是绝对路径或相对路径）
     * @param source  当前命令上下文，用于获取 RegistryAccess / HolderLookup.Provider
     * @return 可写书的 ItemStack，如果反序列化失败则返回 ItemStack.EMPTY
     */
    public static ItemStack createWritableBookFromTxt(Path txtPath, CommandSourceStack source) {
        // —— 1. 尝试读取并让玩家看到路径 ——
        source.sendSystemMessage(Component.literal("§e[BookLoader] 尝试读取文件: " + txtPath.toAbsolutePath()));

        String fullText;
        try {
            fullText = Files.readString(txtPath, StandardCharsets.UTF_8);
            // 如果读取成功，通知玩家
            source.sendSystemMessage(Component.literal("§a[BookLoader] 成功读取文件，长度 "
                    + fullText.getBytes(StandardCharsets.UTF_8).length + " 字节"));
        } catch (IOException e) {
            // 如果读取失败，通知玩家并写入错误
            source.sendSystemMessage(Component.literal("§c[BookLoader] 无法读取文件: "
                    + txtPath.getFileName() + " （请检查路径是否正确）"));
            // 同时也可以打印到控制台
            e.printStackTrace();
            fullText = "§c[错误] 无法读取文件: " + txtPath.getFileName();
        }

        // —— 2. 拆分页 ——
        List<String> pagesText = BookUtil.splitTextToPages(fullText);

        // —— 3. 生成可写书 ——
        ItemStack writableBook = new ItemStack(Items.WRITABLE_BOOK);

        // —— 4. 获取 holder lookup provider ——
        HolderLookup.Provider provider = source.registryAccess();

        // —— 5. 序列化到 NBT ——
        CompoundTag rootNbt = (CompoundTag) writableBook.save(provider);

        // —— 6. 拿到 customTag ——
        CompoundTag customTag = rootNbt.getCompound("tag");
        if (!rootNbt.contains("tag")) {
            rootNbt.put("tag", customTag);
        }

        // —— 7. 写入 pages ——
        ListTag pagesTag = new ListTag();
        for (String pageText : pagesText) {
            String escaped = pageText.replace("\\", "\\\\").replace("\"", "\\\"");
            String json = "{\"text\":\"" + escaped + "\"}";
            pagesTag.add(StringTag.valueOf(json));
        }
        customTag.put("pages", pagesTag);

        // —— 8. 反序列化返回书 ——
        return ItemStack.parseOptional(provider, rootNbt);
    }

}
