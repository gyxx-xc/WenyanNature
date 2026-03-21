package indi.wenyan.setup.config;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.judou.utils.ConfigManager;
import indi.wenyan.judou.utils.IConfigProvider;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
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
        assert container.getEventBus() != null;
        container.getEventBus().addListener((ModConfigEvent.Loading evt) -> {
            if (evt.getConfig().getSpec() == common.spec) {
                common.sync();
            }
        });
        container.getEventBus().addListener((ModConfigEvent.Reloading evt) -> {
            if (evt.getConfig().getSpec() == common.spec) {
                common.sync();
            }
        });
    }

    public static void register(ModContainer container) {
        if (!container.getModId().equals(WenyanProgramming.MODID)) {
            throw new IllegalArgumentException();
        }
        instance = new WenyanConfig(container);
        ConfigManager.registerConfigProvider(judouConfigProvider);
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
        return instance().common.throwEntityLifetime.get();
    }

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

        public final IntValue formationRange;
        public final IntValue pedestalRange;
        public final IntValue runnerRange;

        public final IntValue powerDuration;

        public final IntValue throwEntityLifetime;

        public CommonConfig() {
            var builder = new ModConfigSpec.Builder();

            builder.push("judou");
            sliceStep = define(builder, "slice_step", 10, 5, 20, "x100 instructions");
            maxThread = define(builder, "max_thread", 10, 5, 20);
            watchdogTimeoutAdjust = define(builder, "watchdog_timeout", 1.0, 0.5, 5.0);
            resultMaxSize = define(builder, "result_max_size", 64, 32, 256);
            builder.pop();

            builder.push("in_game");
            formationRange = define(builder, "formation_range", 10, 5, 15);
            pedestalRange = define(builder, "pedestal_range", 3, 1, 5);
            runnerRange = define(builder, "runner_range", 3, 1, 5);
            powerDuration = define(builder, "duration", 20, 2, 40);
            throwEntityLifetime = define(builder, "lifetime", 5, 1, 10);
            builder.pop();

            spec = builder.build();
        }

        public void sync() {
            // TODO: sync
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
