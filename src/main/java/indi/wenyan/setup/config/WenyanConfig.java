package indi.wenyan.setup.config;

import indi.wenyan.WenyanProgramming;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Config(name = WenyanProgramming.MODID)
public final class WenyanConfig {
      public static Screen createConfigScreen(Screen parent) {
          //init
          ConfigBuilder builder = ConfigBuilder.create()
                  .setParentScreen(parent)
                  .setTitle(Component.translatable("config.wenyan_programming.main.title"));
          builder.setSavingRunnable(() -> {});

          //General settings
          ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.wenyan_programming.general.title"));
          ConfigEntryBuilder entryBuilder = builder.entryBuilder();

          general.addEntry(entryBuilder
                  .startStrField(Component.translatable("config.wenyan_programming.general.test"), "false")
                  .setDefaultValue("false")
                  .build());


          //Performance settings
          ConfigCategory performance = builder.getOrCreateCategory(Component.translatable("config.wenyan_programming.performance.title"));
          performance.addEntry(entryBuilder
                  .startIntField(Component.translatable("config.wenyan_programming.performance.thread_limit"), 5)
                  .setTooltip(Component.translatable("config.wenyan_programming.performance.thread_limit.description"))
                  .build());


          //Advanced settings
          ConfigCategory advanced = builder.getOrCreateCategory(Component.translatable("config.wenyan_programming.advanced.title"));
          advanced.addEntry(entryBuilder
                  .startBooleanToggle(Component.translatable("config.wenyan_programming.advanced.debug_mode"), false)
                  .setTooltip(Component.translatable("config.wenyan_programming.advanced.debug_mode.description"))
                  .setDefaultValue(false)
                  .build());

          //Save Logic
          builder.setSavingRunnable(() -> {

          });

          return builder.build();
      }

}
