package indi.wenyan.setup.language;

public enum FunctionMetaText implements ILocalizationEnum {
    BlockingQueueModule$put,
    BlockingQueueModule$take,
    BlockingQueueModule$offer,
    BlockingQueueModule$poll,
    BlockingQueueModule$peek,
    BlockingQueueModule$size,
    BlockingQueueModule$clear,
    BlockModule$search,
    BlockModule$get,
    BlockModule$attach,
    CRAFTING_ARGS,
    PRINT,
    POWER_UP,
    POWER_ANS,
    EntityModule$inspectRange,
    EntityModule$nearby,
    EntityModule$lineOfSight,
    ExplosionModule$lightning,
    ExplosionModule$explode,
    ExplosionModule$ignite,
    ExplosionModule$fireball,
    CORE_START,
    CORE_JOIN,
    CORE_STATUS,
    ItemModule$transfer,
    ItemModule$read,
    SemaphoreModule$acquire,
    SemaphoreModule$release,
    FURNACE_BURN,
    FURNACE_DOUBLE_BURN,
    FURNACE_GET_PROGRESS,
    FURNACE_GET_MAX_PROGRESS,
    PISTON_PUSH,
    PISTON_PULL,
    WorldModule$signalStrength,
    WorldModule$emitSignal,
    WorldModule$changeWeather;

    @Override
    public String getTranslationKey() {
        return "metadata.wenyan_programming." + name();
    }
}
