package indi.wenyan.content.gui.configMenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The General Options screen.
 */
public final class GeneralOptionsScreen {

    private GeneralOptionsScreen() {
        // Do nothing
    }

    /**
     * Gets the config builder.
     *
     * @param parent the previous screen
     * @return the config builder
     */
    public static ConfigBuilder configBuilder(final Screen parent) {

        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.betterf3.title"));

        builder.setSavingRunnable(ModConfigFile.saveRunnable);

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.betterf3.title.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.disable"), GeneralOptions.disableMod)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.disable.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.disableMod = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.auto_start"), GeneralOptions.autoF3)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.auto_start.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.autoF3 = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.space_modules"), GeneralOptions.spaceEveryModule)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.space_modules.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.spaceEveryModule = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.shadow_text"), GeneralOptions.shadowText)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.betterf3.shadow_text.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.shadowText = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.animations"), GeneralOptions.enableAnimations)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.betterf3.animations.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.enableAnimations = newValue)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterf3.animationSpeed"), GeneralOptions.animationSpeed)
                .setDefaultValue(1)
                .setMin(1).setMax(3)
                .setTooltip(Component.translatable("config.betterf3.animationSpeed.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.animationSpeed = newValue)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterf3.fontScale"), GeneralOptions.fontScale)
                .setDefaultValue(1)
                .setMin(0.1).setMax(2)
                .setTooltip(Component.translatable("config.betterf3.fontScale.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.fontScale = newValue)
                .build());

        general.addEntry(entryBuilder.startColorField(Component.translatable("config.betterf3.color.background"), GeneralOptions.backgroundColor)
                .setDefaultValue(0x6F505050)
                .setAlphaMode(true)
                .setTooltip(Component.translatable("config.betterf3.color.background.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.backgroundColor = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.debug_crosshair"), GeneralOptions.hideDebugCrosshair)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.debug_crosshair.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.hideDebugCrosshair = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.sidebar"), GeneralOptions.hideSidebar)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.betterf3.sidebar.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.hideSidebar = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.bossbar"), GeneralOptions.hideBossbar)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.betterf3.bossbar.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.hideBossbar = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.always_enable_profiler"), GeneralOptions.alwaysEnableProfiler)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.always_enable_profiler.tooltip"))
                .setSaveConsumer(newValue -> GeneralOptions.alwaysEnableProfiler = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.always_enable_tps_graph"), GeneralOptions.alwaysEnableTPS)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.always_enable_tps_graph.tooltip"))
                .setSaveConsumer(newValue -> {
                    if (newValue && GeneralOptions.alwaysEnablePing) {
                        GeneralOptions.alwaysEnablePing = false;
                        GeneralOptions.alwaysEnableTPS = true;
                    } else {
                        GeneralOptions.alwaysEnableTPS = newValue;
                    }
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterf3.always_enable_ping_graph"), GeneralOptions.alwaysEnablePing)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.betterf3.always_enable_ping_graph.tooltip"))
                .setSaveConsumer(newValue -> {
                    if (newValue && GeneralOptions.alwaysEnableTPS) {
                        GeneralOptions.alwaysEnableTPS = false;
                        GeneralOptions.alwaysEnablePing = true;
                    } else {
                        GeneralOptions.alwaysEnablePing = newValue;
                    }
                })
                .build());

        builder.transparentBackground();
        return builder;
    }
}
