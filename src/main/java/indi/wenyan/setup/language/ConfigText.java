package indi.wenyan.setup.language;

public enum ConfigText implements ILocalizationEnum {
    Judou,            // 句读（程序运行）
    SliceStep,        // 线程切换长度
    MaxThread,        // 最大线程数
    WatchdogTimeout,  // 超时倍率
    ResultMaxSize,    // 结果堆栈最大长度
    InGame,           // 游戏（物品与世界）
    FormationRange,   // 阵眼范围
    PedestalRange,    // 基石范围
    RunnerRange,      // 符範圍
    Duration,         // 算核消散游戏刻
    Lifetime,         // 投符持續時間
    ;

    public String getName() {
        return name();
    }

    @Override
    public String getTranslationKey() {
        return "wenyan_programming.configuration." + name();
    }
}
