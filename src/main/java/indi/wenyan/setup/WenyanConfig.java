package indi.wenyan.setup;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class WenyanConfig {
      public static Screen createConfigScreen(Screen parent) {
          ConfigBuilder builder = ConfigBuilder.create()
                  .setParentScreen(parent)
                  .setTitle(Component.translatable("config.wenyan_nature.title"));
          builder.setSavingRunnable(() -> {});
          ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.wenyan.general"));
          ConfigEntryBuilder entryBuilder = builder.entryBuilder();

          general.addEntry(entryBuilder.startStrField(Component.translatable("option.examplemod.optionA"), "ABC")
                  .setDefaultValue("This is the default value") // Recommended: Used when user click "Reset"
                  .build()); // Builds the option entry for cloth config

          Screen screen = builder.build();
          return screen;
      }
}
