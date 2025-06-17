package indi.wenyan.content.gui.configMenu;

import indi.wenyan.content.gui.configMenu.ModConfigFile;
import indi.wenyan.content.gui.configMenu.BaseModule;

import indi.wenyan.content.gui.configMenu.EmptyModule;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.apache.commons.lang3.StringUtils;
import indi.wenyan.content.gui.configMenu.SystemModule;

/**
 * The Edit Modules screen.
 */
public final class EditModulesScreen {

    private EditModulesScreen() {
        // Do nothing
    }

    /**
     * Gets the config builder.
     *
     * @param module the module
     * @param parent the parent module screen
     * @return The ConfigBuilder for the add module screen
     */
    public static ConfigBuilder configBuilder(final BaseModule module, final ModulesScreen parent) {

        final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent);

        builder.setSavingRunnable(ModConfigFile.saveRunnable);

        final ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.betterf3" +
                ".category.general"));

        final BooleanListEntry moduleEnabled = entryBuilder.startBooleanToggle(Component.translatable("config" +
                        ".betterf3.module.enable"), module.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.betterf3.module.enable.tooltip"))
                .setSaveConsumer(newValue -> {
                    module.enabled = newValue;
                    module.enabled(newValue);
                })
                .build();

        general.addEntry(moduleEnabled);


        if (module instanceof EmptyModule emptyModule) {

            final IntegerListEntry emptyLines = entryBuilder.startIntField(Component.translatable("config.betterf3" +
                            ".empty_lines"), emptyModule.emptyLines)
                    .setDefaultValue(emptyModule.defaultEmptyLines)
                    .setTooltip(Component.translatable("config.betterf3.empty_lines.tooltip"))
                    .setSaveConsumer(newValue -> {
                        if (newValue > 20) {
                            newValue = 20;
                        } else if (newValue < 1) {
                            newValue = 1;
                        }
                        emptyModule.emptyLines = newValue;
                    })
                    .build();

            general.addEntry(emptyLines);

        }


//
//            final StringListEntry timeFormat = entryBuilder
//                    .startStrField(Component.translatable("config.betterf3.time_format"), systemModule.timeFormat)
//                    .setDefaultValue(systemModule.defaultTimeFormat)
//                    .setTooltip(Component.translatable("config.betterf3.time_format.tooltip"))
//                    .setSaveConsumer(newValue -> systemModule.timeFormat = newValue)
//                    .build();
//
//            general.addEntry(timeFormat);
//        }
//
//        if (module.nameColor != null && module.defaultNameColor != null) {
//            final ColorEntry nameColor = entryBuilder.startColorField(Component.translatable("config.betterf3" +
//                            ".color.name"), module.nameColor.getValue())
//                    .setDefaultValue(module.defaultNameColor.getValue())
//                    .setTooltip(Component.translatable("config.betterf3.color.name.tooltip"))
//                    .setSaveConsumer(newValue -> module.nameColor = TextColor.fromRgb(newValue))
//                    .build();
//
//            general.addEntry(nameColor);
//        }
//
//        if (module.valueColor != null && module.defaultValueColor != null) {
//            final ColorEntry valueColor = entryBuilder.startColorField(Component.translatable("config.betterf3" +
//                            ".color.value"), module.valueColor.getValue())
//                    .setDefaultValue(module.defaultValueColor.getValue())
//                    .setTooltip(Component.translatable("config.betterf3.color.value.tooltip"))
//                    .setSaveConsumer(newValue -> module.valueColor = TextColor.fromRgb(newValue))
//                    .build();
//
//            general.addEntry(valueColor);
//        }
//
//        if (module.lines().size() > 1) {
//            for (final DebugLine line : module.lines()) {
//
//                if (line.id().equals("") || line.id().equals("nothing")) {
//                    continue;
//                }
//
//                Component name = Component.translatable("text.betterf3.line." + line.id());
//
//                if (name.getString().equals("")) {
//                    name = Component.nullToEmpty(StringUtils.capitalize(line.id().replace("_", " ")));
//                }
//
//                final BooleanListEntry enabled = entryBuilder.startBooleanToggle(name, line.enabled)
//                        .setDefaultValue(true)
//                        .setTooltip(Component.translatable("config.betterf3.disable_line.tooltip"))
//                        .setSaveConsumer(newValue -> line.enabled = newValue)
//                        .build();
//
//                general.addEntry(enabled);
//
//            }
//        }
//
//        builder.transparentBackground();
      return builder;
    }
}
