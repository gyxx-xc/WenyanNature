package indi.wenyan.setup.language;

public enum FunctionMetaText implements ILocalizationEnum {
    BlockingQueueModulePut, BlockingQueueModuleTake, BlockingQueueModuleOffer, BlockingQueueModulePoll,
    BlockingQueueModulePeek, BlockingQueueModuleSize, BlockingQueueModuleClear,

    BlockModuleSearch, BlockModuleGet, BlockModuleAttach,

    CraftingArgs, Print,
    PowerUp, PowerAns,

    EntityModuleInspectRange, EntityModuleNearby, EntityModuleLineOfSight,

    ExplosionModuleLightning, ExplosionModuleExplode, ExplosionModuleIgnite, ExplosionModuleFireball,

    CoreStart, CoreJoin, CoreStatus,

    ItemModuleTransfer, ItemModuleRead,

    SemaphoreModuleAcquire, SemaphoreModuleRelease,

    FurnaceBurn, FurnaceDoubleBurn, FurnaceGetProgress, FurnaceGetMaxProgress,

    PistonPush, PistonPull,

    WorldModuleSignalStrength, WorldModuleEmitSignal, WorldModuleChangeWeather,

    // HERE
    BitModuleLeftShift, BitModuleRightShift, BitModuleZeroFillRightShift, BitModuleBitAnd, BitModuleBitOr,
    BitModuleBitXor, BitModuleBitNand, BitModuleBitNot,

    CollectionModuleDisjoint, CollectionModuleIntersection, CollectionModuleDifference, CollectionModuleReverse, CollectionModuleSort,
    CollectionModuleContains, CollectionModuleMax, CollectionModuleMin,

    MathModulePI, MathModuleTAU, MathModuleHalfPi, MathModuleQuarterPi, MathModuleE, MathModuleEuler, MathModuleGoldenRatio,
    MathModuleSqrt2, MathModuleLog2, MathModuleLog10, MathModuleSin, MathModuleCos, MathModuleAsin,
    MathModuleAcos, MathModuleTan, MathModuleAtan, MathModuleAtan2, MathModuleHypot, MathModuleLog, MathModuleExp,
    MathModulePow, MathModuleSqrt, MathModuleAbs, MathModuleCeil, MathModuleFloor, MathModuleRound, MathModuleSignum,

    RandomModuleNextInt, RandomModuleNextDouble, RandomModuleNextTriangle, RandomModuleNextBoolean,

    StringModuleLength, StringModuleCharAt, StringModuleIndexOf, StringModuleSplit, StringModuleReplace,
    StringModuleReverse, StringModuleTrim, StringModuleContains, StringModuleStartsWith, StringModuleEndsWith,

    Vec3ModuleObject;
    // TO HERE

    @Override
    public String getTranslationKey() {
        return "metadata.wenyan_programming." + name();
    }
}
