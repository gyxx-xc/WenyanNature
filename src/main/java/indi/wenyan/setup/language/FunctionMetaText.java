package indi.wenyan.setup.language;

public enum FunctionMetaText implements ILocalizationEnum {
    BlockingQueueModulePut,
    BlockingQueueModuleTake,
    BlockingQueueModuleOffer,
    BlockingQueueModulePoll,
    BlockingQueueModulePeek,
    BlockingQueueModuleSize,
    BlockingQueueModuleClear,
    BlockModuleSearch,
    BlockModuleGet,
    BlockModuleAttach,
    CraftingArgs,
    Print,
    PowerUp,
    PowerAns,
    EntityModuleInspectRange,
    EntityModuleNearby,
    EntityModuleLineOfSight,
    ExplosionModuleLightning,
    ExplosionModuleExplode,
    ExplosionModuleIgnite,
    ExplosionModuleFireball,
    CoreStart,
    CoreJoin,
    CoreStatus,
    ItemModuleTransfer,
    ItemModuleRead,
    SemaphoreModuleAcquire,
    SemaphoreModuleRelease,
    FurnaceBurn,
    FurnaceDoubleBurn,
    FurnaceGetProgress,
    FurnaceGetMaxProgress,
    PistonPush,
    PistonPull,
    WorldModuleSignalStrength,
    WorldModuleEmitSignal,
    WorldModuleChangeWeather;

    @Override
    public String getTranslationKey() {
        return "metadata.wenyan_programming." + name();
    }
}
