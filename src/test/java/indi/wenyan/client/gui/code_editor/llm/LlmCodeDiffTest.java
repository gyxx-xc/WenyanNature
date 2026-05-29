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

        assertThat(diff).isNotEmpty();
    }
}
