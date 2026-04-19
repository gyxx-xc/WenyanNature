package indi.wenyan.setup.language;

public enum GuiText implements ILocalizationEnum {
    HoldShift,
    NarrateEditBox, // narrator
    NarrateSnippet,
    FloatNoteName,
    Done,
    Lock,
    CreativeTabTitle,
    EnterToInput, // Enter to input (able to show small)
    FuNamePrompt,
    FurnaceTitle,
    JeiAnswerTitle,
    AiPromptLabel,    // prefix shown before the AI prompt input box
    AiGenerateButton, // label on the "generate" button
    AiGenerating,     // shown while waiting for DeepSeek response
    AiError;          // shown when DeepSeek call fails

    @Override
    public String getTranslationKey() {
        return "gui.wenyan_programming." + name();
    }
}
