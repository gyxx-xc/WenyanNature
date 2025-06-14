package indi.wenyan.setup;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class WenyanConfig {
      public static Screen createConfigScreen(Screen parent) {
          //init
          ConfigBuilder builder = ConfigBuilder.create()
                  .setParentScreen(parent)
                  .setTitle(Component.translatable("config.wenyan_nature.main.title"));
          builder.setSavingRunnable(() -> {});

          //General settings
          ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.wenyan_nature.general.title"));
          ConfigEntryBuilder entryBuilder = builder.entryBuilder();

          general.addEntry(entryBuilder
                  .startStrField(Component.translatable("config.wenyan_nature.general.test"), "false")
                  .setDefaultValue("false")
                  .build());


          //Performance settings
          ConfigCategory performance = builder.getOrCreateCategory(Component.translatable("config.wenyan_nature.performance.title"));
          performance.addEntry(entryBuilder
                  .startIntField(Component.translatable("config.wenyan_nature.performance.thread_limit"), 5)
                  .setTooltip(Component.translatable("config.wenyan_nature.performance.thread_limit.description"))
                  .build());


          //Advanced settings
          ConfigCategory advanced = builder.getOrCreateCategory(Component.translatable("config.wenyan_nature.advanced.title"));
          advanced.addEntry(entryBuilder
                  .startBooleanToggle(Component.translatable("config.wenyan_nature.advanced.debug_mode"), false)
                  .setTooltip(Component.translatable("config.wenyan_nature.advanced.debug_mode.description"))
                  .setDefaultValue(false)
                  .build());

          //Save Logic
          builder.setSavingRunnable(() -> {

          });

          Screen screen = builder.build();
          return screen;
      }

}
