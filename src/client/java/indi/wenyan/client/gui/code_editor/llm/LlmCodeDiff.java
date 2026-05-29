package indi.wenyan.client.gui.code_editor.llm;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LlmCodeDiff {
    ;

    private static final long MAX_LCS_CELLS = 2_000_000L;

    public static @NotNull List<Line> diff(@NotNull String oldCode, @NotNull String newCode) {
        List<String> oldLines = splitLines(oldCode);
        List<String> newLines = splitLines(newCode);
        List<Line> lines = new ArrayList<>();

        int prefixLength = commonPrefixLength(oldLines, newLines);
        int suffixLength = commonSuffixLength(oldLines, newLines, prefixLength);

        for (int i = 0; i < prefixLength; i++) {
            lines.add(new Line(Type.UNCHANGED, i + 1, i + 1, oldLines.get(i)));
        }

        int oldMiddleStart = prefixLength;
        int oldMiddleEnd = oldLines.size() - suffixLength;
        int newMiddleStart = prefixLength;
        int newMiddleEnd = newLines.size() - suffixLength;
        appendMiddleDiff(lines, oldLines, newLines, oldMiddleStart, oldMiddleEnd, newMiddleStart, newMiddleEnd);

        for (int i = 0; i < suffixLength; i++) {
            int oldIndex = oldMiddleEnd + i;
            int newIndex = newMiddleEnd + i;
            lines.add(new Line(Type.UNCHANGED, oldIndex + 1, newIndex + 1, oldLines.get(oldIndex)));
        }

        return lines;
    }

    private static void appendMiddleDiff(List<Line> lines, List<String> oldLines, List<String> newLines,
                                         int oldStart, int oldEnd, int newStart, int newEnd) {
        int oldSize = oldEnd - oldStart;
        int newSize = newEnd - newStart;
        if ((long) (oldSize + 1) * (newSize + 1) > MAX_LCS_CELLS) {
            appendFallbackDiff(lines, oldLines, newLines, oldStart, oldEnd, newStart, newEnd);
            return;
        }

        int[][] lcs = buildLcsTable(oldLines, newLines, oldStart, oldEnd, newStart, newEnd);
        int oldIndex = 0;
        int newIndex = 0;
        while (oldIndex < oldSize || newIndex < newSize) {
            int absoluteOldIndex = oldStart + oldIndex;
            int absoluteNewIndex = newStart + newIndex;
            if (oldIndex < oldSize && newIndex < newSize &&
                    oldLines.get(absoluteOldIndex).equals(newLines.get(absoluteNewIndex))) {
                lines.add(new Line(Type.UNCHANGED, absoluteOldIndex + 1, absoluteNewIndex + 1, oldLines.get(absoluteOldIndex)));
                oldIndex++;
                newIndex++;
            } else if (newIndex < newSize &&
                    (oldIndex == oldSize || lcs[oldIndex][newIndex + 1] > lcs[oldIndex + 1][newIndex])) {
                lines.add(new Line(Type.ADDED, 0, absoluteNewIndex + 1, newLines.get(absoluteNewIndex)));
                newIndex++;
            } else {
                lines.add(new Line(Type.REMOVED, absoluteOldIndex + 1, 0, oldLines.get(absoluteOldIndex)));
                oldIndex++;
            }
        }
    }

    private static void appendFallbackDiff(List<Line> lines, List<String> oldLines, List<String> newLines,
                                           int oldStart, int oldEnd, int newStart, int newEnd) {
        for (int oldIndex = oldStart; oldIndex < oldEnd; oldIndex++) {
            lines.add(new Line(Type.REMOVED, oldIndex + 1, 0, oldLines.get(oldIndex)));
        }
        for (int newIndex = newStart; newIndex < newEnd; newIndex++) {
            lines.add(new Line(Type.ADDED, 0, newIndex + 1, newLines.get(newIndex)));
        }
    }

    private static int commonPrefixLength(List<String> oldLines, List<String> newLines) {
        int maxPrefix = Math.min(oldLines.size(), newLines.size());
        int prefixLength = 0;
        while (prefixLength < maxPrefix && oldLines.get(prefixLength).equals(newLines.get(prefixLength))) {
            prefixLength++;
        }
        return prefixLength;
    }

    private static int commonSuffixLength(List<String> oldLines, List<String> newLines, int prefixLength) {
        int oldIndex = oldLines.size() - 1;
        int newIndex = newLines.size() - 1;
        int suffixLength = 0;
        while (oldIndex >= prefixLength && newIndex >= prefixLength && oldLines.get(oldIndex).equals(newLines.get(newIndex))) {
            suffixLength++;
            oldIndex--;
            newIndex--;
        }
        return suffixLength;
    }

    private static int[][] buildLcsTable(List<String> oldLines, List<String> newLines,
                                         int oldStart, int oldEnd, int newStart, int newEnd) {
        int oldSize = oldEnd - oldStart;
        int newSize = newEnd - newStart;
        int[][] lcs = new int[oldSize + 1][newSize + 1];
        for (int oldIndex = oldSize - 1; oldIndex >= 0; oldIndex--) {
            for (int newIndex = newSize - 1; newIndex >= 0; newIndex--) {
                if (oldLines.get(oldStart + oldIndex).equals(newLines.get(newStart + newIndex))) {
                    lcs[oldIndex][newIndex] = lcs[oldIndex + 1][newIndex + 1] + 1;
                } else {
                    lcs[oldIndex][newIndex] = Math.max(lcs[oldIndex + 1][newIndex], lcs[oldIndex][newIndex + 1]);
                }
            }
        }
        return lcs;
    }

    private static @NotNull List<String> splitLines(@NotNull String code) {
        if (code.isEmpty())
            return List.of();
        String[] lines = code.split("\\R", -1);
        if (lines.length > 0 && lines[lines.length - 1].isEmpty()) {
            lines = Arrays.copyOf(lines, lines.length - 1);
        }
        return Arrays.asList(lines);
    }

    public record Line(@NotNull Type type, int oldLineNo, int newLineNo, @NotNull String text) {
    }

    public enum Type {
        UNCHANGED,
        ADDED,
        REMOVED
    }
}
