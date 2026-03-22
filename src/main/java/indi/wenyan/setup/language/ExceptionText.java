package indi.wenyan.setup.language;

public enum ExceptionText implements LocalizationEnum {
    NotFindFu,
    CantStart,
    LockHoldAlready,
    LockNotHold,
    NeedBlockItem,
    NeedItemCapability,
    ArgsNeedWeather,
    InvaildDirection,
    FailedToPlacePiston,
    FailedToMoveBlock,
    DeviceRemoved,
    ImportNotFound,
    NoConnectDirection,

    ArgsNumWrong,
    ArgsNumWrongRange,
    ArgsTypeWrong,
    NoAttribute,
    ;

    @Override
    public String getTranslationKey() {
        return "error.wenyan_programming."+ name();
    }
}
