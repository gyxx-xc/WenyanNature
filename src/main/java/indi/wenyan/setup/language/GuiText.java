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
    FuNamePrompt;

    @Override
    public String getTranslationKey() {
        return "gui.wenyan_programming." + name();
    }
}
