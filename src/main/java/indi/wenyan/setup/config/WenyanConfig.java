package indi.wenyan.setup.config;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.judou.utils.UtilManager;
import indi.wenyan.judou.utils.config.IConfigProvider;
import indi.wenyan.judou.utils.function.ChineseUtils;
import indi.wenyan.setup.language.ConfigText;
import lombok.Getter;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@SuppressWarnings({"unused", "SameParameterValue"})
public final class WenyanConfig {
    private final ClientConfig client = new ClientConfig();
    private final CommonConfig common = new CommonConfig();

    private static WenyanConfig instance;

    private WenyanConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, client.spec);
        container.registerConfig(ModConfig.Type.COMMON, common.spec);
    }

    public static void register(ModContainer container) {
        if (!container.getModId().equals(WenyanProgramming.MODID)) {
            throw new IllegalArgumentException();
        }
        instance = new WenyanConfig(container);
        UtilManager.setConfig(judouConfigProvider);
    }

    public static WenyanConfig instance() {
        return instance;
    }

    public void save() {
        common.spec.save();
        client.spec.save();
    }

    public static int getFormationRange() {
        return instance().common.formationRange.get();
    }

    public static int getPedestalRange() {
        return instance().common.pedestalRange.get();
    }

    public static int getRunnerRange() {
        return instance().common.runnerRange.get();
    }

    public static int getPowerDuration() {
        return instance().common.powerDuration.get();
    }

    public static int getThrowEntityLifetime() {
        return instance().common.throwEntityLifetime.get() * 20;
    }

    @Getter
    private static final IConfigProvider judouConfigProvider = new IConfigProvider() {
        @Override
        public int getMaxThread() {
            return instance().common.maxThread.get();
        }

        @Override
        public int getMaxSlice() {
            return instance().common.sliceStep.get() * 100;
        }

        @Override
        public int getWatchdogTimeout() {
            WenyanConfig wenyanConfig = instance();
            return (int) (wenyanConfig.common.sliceStep.get() * wenyanConfig.common.watchdogTimeoutAdjust.get());
        }

        @Override
        public int getResultMaxSize() {
            return instance().common.resultMaxSize.get();
        }

        @Override
        public int getMaxRecursionDepth() {
            return instance().common.maxRecursionDepth.get() * 100;
        }

        @Override
        public int getMaxQueueSize() {
            return instance().common.maxQueueSize.get();
        }

        @Override
        public int getMaxQueueSizePerTick() {
            return instance().common.maxQueueSizePerTick.get();
        }

        @Override
        public boolean useLegacyRunner() {
            return instance().common.useLegacyRunner.get();
        }

        @Override
        public boolean convertCode() {
            return instance().common.useTraditionalConversion.get();
        }

        @Override
        public ChineseUtils.SymbolFormat symbolConversion() {
            return ChineseUtils.SymbolFormat.TRADITIONAL;
            // FIXME: has static vars depends on this (before config loaded).
//            return instance().common.symbolConversion.get();
        }
    };

    private static class ClientConfig {
        private final ModConfigSpec spec;

        public ClientConfig() {
            var builder = new ModConfigSpec.Builder();
            spec = builder.build();
        }
    }

    private static class CommonConfig {
        private final ModConfigSpec spec;

        public final IntValue sliceStep;
        public final IntValue maxThread;
        public final DoubleValue watchdogTimeoutAdjust;
        public final IntValue resultMaxSize;
        public final IntValue maxRecursionDepth;
        public final BooleanValue useLegacyRunner;
        public final BooleanValue useTraditionalConversion;
        public final EnumValue<ChineseUtils.SymbolFormat> symbolConversion;
        public final IntValue maxQueueSize;
        public final IntValue maxQueueSizePerTick;

        public final IntValue formationRange;
        public final IntValue pedestalRange;
        public final IntValue runnerRange;

        public final IntValue powerDuration;

        public final IntValue throwEntityLifetime;

        public CommonConfig() {
            var builder = new ModConfigSpec.Builder();

            builder.push(ConfigText.Judou.getName());
            sliceStep = define(builder, ConfigText.SliceStep.getName(), 10, 5, 20,
                    "x100");
            maxThread = define(builder, ConfigText.MaxThread.getName(), 10, 5, 20);
            watchdogTimeoutAdjust = define(builder, ConfigText.WatchdogTimeout.getName(), 1.0, 0.5, 5.0);
            resultMaxSize = define(builder, ConfigText.ResultMaxSize.getName(), 64, 32, 256);
            maxRecursionDepth = define(builder, ConfigText.MaxRecursionDepth.getName(), 30, 1, 50,
                    "x100");
            useLegacyRunner = define(builder, ConfigText.UseLegacyRunner.getName(), false);
            useTraditionalConversion = define(builder, ConfigText.UseTraditionalConversion.getName(), false);
            builder.gameRestart();
            symbolConversion = defineEnum(builder, ConfigText.SymbolConversion.getName(), ChineseUtils.SymbolFormat.TRADITIONAL,
                    "symbol inside 「」");
            maxQueueSize = define(builder, ConfigText.MaxQueueSize.getName(), 50, 30, 100);
            maxQueueSizePerTick = define(builder, ConfigText.MaxQueueSizePerTick.getName(), 20, 10, 50);
            builder.pop();

            builder.push(ConfigText.InGame.getName());
            formationRange = define(builder, ConfigText.FormationRange.getName(), 10, 5, 15);
            pedestalRange = define(builder, ConfigText.PedestalRange.getName(), 3, 1, 5);
            runnerRange = define(builder, ConfigText.RunnerRange.getName(), 3, 1, 5);
            powerDuration = define(builder, ConfigText.Duration.getName(), 20, 2, 40);
            throwEntityLifetime = define(builder, ConfigText.Lifetime.getName(), 5, 1, 10);
            builder.pop();

            spec = builder.build();
        }
    }

    private static BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue,
                                       String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    private static BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    private static DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue) {
        return define(builder, name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private static DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    private static DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, double min,
                                      double max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    private static DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue, double min,
                                      double max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max,
                                   String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    private static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue) {
        return define(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static <T extends Enum<T>> EnumValue<T> defineEnum(ModConfigSpec.Builder builder, String name,
                                                               T defaultValue) {
        return builder.defineEnum(name, defaultValue);
    }

    private static <T extends Enum<T>> EnumValue<T> defineEnum(ModConfigSpec.Builder builder, String name,
                                                               T defaultValue, String comment) {
        builder.comment(comment);
        return defineEnum(builder, name, defaultValue);
    }
}
