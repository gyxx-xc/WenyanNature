package indi.wenyan.setup.language;

public enum FunctionMetaText implements ILocalizationEnum {
    ExplosionLightning;

    @Override
    public String getTranslationKey() {
        return "metadata.wenyan_programming." + name();
    }
}
