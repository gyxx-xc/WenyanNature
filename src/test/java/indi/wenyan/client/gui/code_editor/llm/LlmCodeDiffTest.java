package indi.wenyan.client.gui.code_editor.llm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmCodeDiffTest {

    @Test
    void testEmptyOldCodeAddsAllNewLines() {
        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff("", "吾有一數。\n書之。");

        assertThat(diff).containsExactly(
                new LlmCodeDiff.Line(LlmCodeDiff.Type.ADDED, 0, 1, "吾有一數。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.ADDED, 0, 2, "書之。")
        );
    }

    @Test
    void testEmptyNewCodeRemovesAllOldLines() {
        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff("吾有一數。\n書之。", "");

        assertThat(diff).containsExactly(
                new LlmCodeDiff.Line(LlmCodeDiff.Type.REMOVED, 1, 0, "吾有一數。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.REMOVED, 2, 0, "書之。")
        );
    }

    @Test
    void testSingleLineChangeRemovesThenAdds() {
        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff("夫一。書之。", "夫二。書之。");

        assertThat(diff).containsExactly(
                new LlmCodeDiff.Line(LlmCodeDiff.Type.REMOVED, 1, 0, "夫一。書之。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.ADDED, 0, 1, "夫二。書之。")
        );
    }

    @Test
    void testMiddleInsertAndRemoveKeepsLineNumbers() {
        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff(
                "夫一。書之。\n夫二。書之。\n夫三。書之。",
                "夫一。書之。\n夫四。書之。\n夫三。書之。"
        );

        assertThat(diff).containsExactly(
                new LlmCodeDiff.Line(LlmCodeDiff.Type.UNCHANGED, 1, 1, "夫一。書之。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.REMOVED, 2, 0, "夫二。書之。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.ADDED, 0, 2, "夫四。書之。"),
                new LlmCodeDiff.Line(LlmCodeDiff.Type.UNCHANGED, 3, 3, "夫三。書之。")
        );
    }

    @Test
    void testTrailingNewlineDoesNotCrash() {
        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff("夫一。書之。\n", "夫一。書之。");

        assertThat(diff).containsExactly(
                new LlmCodeDiff.Line(LlmCodeDiff.Type.UNCHANGED, 1, 1, "夫一。書之。")
        );
    }

    @Test
    void testLargeChangedMiddleFallsBackWithoutLcsExplosion() {
        String oldCode = "same-start\n" + numberedLines("old", 1500) + "\nsame-end";
        String newCode = "same-start\n" + numberedLines("new", 1500) + "\nsame-end";

        List<LlmCodeDiff.Line> diff = LlmCodeDiff.diff(oldCode, newCode);

        assertThat(diff.get(0)).isEqualTo(new LlmCodeDiff.Line(LlmCodeDiff.Type.UNCHANGED, 1, 1, "same-start"));
        assertThat(diff.get(diff.size() - 1)).isEqualTo(new LlmCodeDiff.Line(LlmCodeDiff.Type.UNCHANGED, 1502, 1502, "same-end"));
        assertThat(diff).filteredOn(line -> line.type() == LlmCodeDiff.Type.REMOVED).hasSize(1500);
        assertThat(diff).filteredOn(line -> line.type() == LlmCodeDiff.Type.ADDED).hasSize(1500);
    }

    private static String numberedLines(String prefix, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(prefix).append('-').append(i);
        }
        return builder.toString();
    }
}
