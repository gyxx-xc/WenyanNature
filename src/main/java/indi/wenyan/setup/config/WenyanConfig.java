package indi.wenyan.setup.config;

@SuppressWarnings("ALL")
//@Config(name = WenyanProgramming.MODID)
public final class WenyanConfig {
    // RTFM Use https://shedaniel.gitbook.io/cloth-config/auto-config/introduction-to-auto-config-1u

/*    public static Screen createConfigScreen(Screen parent) {
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
      }*/

}
