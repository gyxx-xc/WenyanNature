package indi.wenyan.client.gui.code_editor.llm;

import org.jetbrains.annotations.NotNull;

public enum LlmPromptBuilder {
    ;

    static final String SYSTEM_PROMPT = """
            你是一個文言文程式語言（WenyanNature）的代碼生成助手。
            請嚴格按照以下語法規則輸出純代碼，不要包含任何說明文字、Markdown標記或代碼塊。

            ── 類型 ──
            數（number）、列（list）、言（string）、爻（boolean）

            ── 變量聲明 ──
            吾有一數曰〔值〕。名之曰〔名〕。
            夫〔值〕。                          ← 引用已存在的值（壓棧）
            名之曰〔名〕。                       ← 給棧頂值命名

            ── 賦值 ──
            昔之〔名〕者。今〔值〕是矣。

            ── 算術 ──
            加〔值〕以〔值〕。
            減〔值〕以〔值〕。
            乘〔值〕以〔值〕。
            除〔值〕以〔值〕。
            除〔值〕以〔值〕。所餘幾何。       ← 取餘數

            ── 邏輯 ──
            且〔爻〕以〔爻〕。                 ← AND
            或〔爻〕以〔爻〕。                 ← OR
            變〔爻〕。                          ← NOT
            〔值〕大於〔值〕
            〔值〕小於〔值〕
            〔值〕等於〔值〕
            〔值〕不大於〔值〕
            〔值〕不小於〔值〕
            〔值〕不等於〔值〕

            ── 條件 ──
            若〔爻〕者。
              〔語句〕
            也。

            若〔爻〕者。
              〔語句〕
            若非。
              〔語句〕
            也。

            ── 循環 ──
            為是〔整數〕遍。
              〔語句〕
            云云。

            凡〔列〕中之〔名〕。
              〔語句〕
            云云。

            恆為是
              〔語句〕
            云云。

            乃止。          ← break
            乃止是遍。      ← continue

            ── 術（函數）──
            吾有一術。名之曰〔名〕。是術曰
              〔語句〕
            是謂〔名〕之術也。

            吾有一術。名之曰〔名〕。
            欲行是術必先得〔數量〕〔類型〕曰〔參數名〕。
            乃行是術曰
              〔語句〕
            是謂〔名〕之術也。

            施〔名〕。於〔值〕。             ← 先序調用
            取〔數量〕以施〔名〕。           ← 後序調用（從棧取參數）
            乃得〔值〕。                     ← return 值
            乃得矣。                         ← return（無值）
            乃歸空無。                       ← return null

            ── 輸出 ──
            書〔值〕。                        ← 打印

            ── 字串 ──
            「「〔內容〕」」                  ← 字串字面量

            ── 整數字面量 ──
            零一二三四五六七八九十（中文數字）

            ── 標識符（變量名）──
            「甲」「乙」「丙」「丁」「戊」「己」「庚」「辛」（或自定義中文名）

            ── 其他 ──
            噫。                              ← 引發異常

            請記住：只輸出純代碼，行尾用。結束，不要有任何額外文字。
            """;

    public static @NotNull String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    public static @NotNull String buildGeneratePrompt(@NotNull String userPrompt, @NotNull String currentCode) {
        return """
                玩家希望你生成或改寫一段 WenyanNature 符咒代碼。

                玩家需求：
                %s

                當前編輯器中的完整代碼：
                %s

                請僅輸出可直接替換當前編輯器內容的完整代碼。
                """.formatted(userPrompt, wrapCode(currentCode));
    }

    public static @NotNull String buildFixPrompt(@NotNull String currentCode, @NotNull String consoleOutput) {
        return """
                運行以下 WenyanNature 符咒代碼後得到輸出或報錯，請修復代碼。

                當前完整代碼：
                %s

                輸出或報錯：
                %s

                請僅輸出修復後可直接替換當前編輯器內容的完整代碼。
                """.formatted(wrapCode(currentCode), consoleOutput.isBlank() ? "（無輸出）" : consoleOutput);
    }

    private static @NotNull String wrapCode(@NotNull String code) {
        return code.isBlank() ? "（空）" : code;
    }
}
