package indi.wenyan.judou.utils.language;

public enum ExceptionText implements JudouLocalizationEnum {
    ExampleError("the origin text"),
    ;

    // STUB: the string of old system
    @Deprecated(forRemoval = true)
    final String originText;

    ExceptionText(String originText) {
        this.originText = originText;
    }

    @Override
    public String getTranslationKey() {
        return name();
    }
}
