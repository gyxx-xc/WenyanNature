package indi.wenyan.setup.language;

public enum ExceptionText implements ILocalizationEnum {
    NotFindFu,             // 謬：不識此符
    CantStart,             // 謬：不可始%s
    LockHoldAlready,       // 謬：線程已持鎖
    LockNotHold,           // 謬：線程未持鎖
    NeedBlockItem,         // 謬：參數需方塊物
    NeedItemCapability,    // 謬：需持物
    ArgsNeedWeather,       // 謬：參數須為「「晴」」「「雨」」「「雷」」
    InvaildDirection,      // 謬：無效之方塊向
    FailedToPlacePiston,   // 謬：置活塞敗
    FailedToMoveBlock,     // 謬：移方塊敗
    DeviceRemoved,         // 謬：器已除
    ImportNotFound,        // 謬：未尋之籍%s
    NoConnectDirection,    // 謬：無連向
    AlreadyRun,            // 謬：已運行
    PackageAlreadtRegistered//Warning: package %s is already registered
    ;

    @Override
    public String getTranslationKey() {
        return "error.wenyan_programming." + name();
    }
}
