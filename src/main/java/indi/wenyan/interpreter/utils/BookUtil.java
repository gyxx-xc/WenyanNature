package indi.wenyan.interpreter.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类：把一段长文本拆分为多页，供书的 NBT 写入。
 */
public class BookUtil {

    // 每页最大字符数（包括换行符），可根据实际效果自行调整
    private static final int MAX_CHARS_PER_PAGE = 240;

    /**
     * 将 fullText 按行拆分，再按 MAX_CHARS_PER_PAGE 拆成多页。
     * @param fullText 整个 TXT 的内容
     * @return 一个 List<String>，每个元素是一页的纯文本（不带 JSON 组件格式）
     */
    public static List<String> splitTextToPages(String fullText) {
        List<String> pages = new ArrayList<>();
        if (fullText == null || fullText.isEmpty()) {
            return pages;
        }

        // 用正则 "\\R" 来匹配任意平台的换行符，把大文本拆成多行
        String[] lines = fullText.split("\\R");
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            // 如果单行就超出了 MAX_CHARS_PER_PAGE，先把 sb 作为一页写入，再把这一行硬拆
            if (line.length() > MAX_CHARS_PER_PAGE) {
                if (sb.length() > 0) {
                    pages.add(sb.toString());
                    sb.setLength(0);
                }
                // 硬拆这一行
                int start = 0;
                while (start < line.length()) {
                    int end = Math.min(start + MAX_CHARS_PER_PAGE, line.length());
                    pages.add(line.substring(start, end));
                    start = end;
                }
                continue;
            }

            // 如果加上这行后超过一页限制，就先把 sb 放到 pages，再新建 sb
            if (sb.length() + line.length() > MAX_CHARS_PER_PAGE) {
                pages.add(sb.toString());
                sb.setLength(0);
            }

            // 如果 sb 里已有内容，就先加换行
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(line);
        }

        // 循环结束后，将 sb 剩余内容当作最后一页
        if (sb.length() > 0) {
            pages.add(sb.toString());
        }
        return pages;
    }
}
