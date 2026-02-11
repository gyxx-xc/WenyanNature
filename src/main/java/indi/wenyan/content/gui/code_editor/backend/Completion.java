package indi.wenyan.content.gui.code_editor.backend;

import indi.wenyan.judou.utils.ChineseUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record Completion(String content) {

    public static List<Completion> getCompletions(String string) {
        if (string.isEmpty()) return Collections.emptyList();
        try {
            double number = Double.parseDouble(string);
            return List.of(new Completion(ChineseUtils.toChinese(number)));
        } catch (NumberFormatException ignored) {
            // return normal completion
        }
        try {
            BigInteger number = new BigInteger(string);
            return List.of(new Completion(ChineseUtils.toChinese(number)));
        } catch (NumberFormatException ignored) {
            // return normal completion
        }
        return generated_Completions.map.subMap(string, string + Character.MAX_VALUE).values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public static boolean isCharHandleable(char c) {
        return ('0' <= c && c <= '9')
                || ('a' <= c && c <= 'z')
                || ('A' <= c && c <= 'Z')
                || c == '"'
                || c == '\''
                || c == '.';
    }
}
