package indi.wenyan.client.gui.code_editor.llm;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LlmCodeDiff {
    ;

    public static @NotNull List<Line> diff(@NotNull String oldCode, @NotNull String newCode) {
        List<String> oldLines = splitLines(oldCode);
        List<String> newLines = splitLines(newCode);
        int[][] lcs = buildLcsTable(oldLines, newLines);
        List<Line> lines = new ArrayList<>();

        int oldIndex = 0;
        int newIndex = 0;
        while (oldIndex < oldLines.size() || newIndex < newLines.size()) {
            if (oldIndex < oldLines.size() && newIndex < newLines.size() &&
                    oldLines.get(oldIndex).equals(newLines.get(newIndex))) {
                lines.add(new Line(Type.UNCHANGED, oldIndex + 1, newIndex + 1, oldLines.get(oldIndex)));
                oldIndex++;
                newIndex++;
            } else if (newIndex < newLines.size() &&
                    (oldIndex == oldLines.size() || lcs[oldIndex][newIndex + 1] > lcs[oldIndex + 1][newIndex])) {
                lines.add(new Line(Type.ADDED, 0, newIndex + 1, newLines.get(newIndex)));
                newIndex++;
            } else {
                lines.add(new Line(Type.REMOVED, oldIndex + 1, 0, oldLines.get(oldIndex)));
                oldIndex++;
            }
        }

        return lines;
    }

    private static int[][] buildLcsTable(List<String> oldLines, List<String> newLines) {
        int[][] lcs = new int[oldLines.size() + 1][newLines.size() + 1];
        for (int oldIndex = oldLines.size() - 1; oldIndex >= 0; oldIndex--) {
            for (int newIndex = newLines.size() - 1; newIndex >= 0; newIndex--) {
                if (oldLines.get(oldIndex).equals(newLines.get(newIndex))) {
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
        return Arrays.asList(code.split("\\R", -1));
    }

    public record Line(@NotNull Type type, int oldLineNo, int newLineNo, @NotNull String text) {
    }

    public enum Type {
        UNCHANGED,
        ADDED,
        REMOVED
    }
}
