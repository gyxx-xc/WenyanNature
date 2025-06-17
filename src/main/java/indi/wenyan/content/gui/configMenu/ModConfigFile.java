package indi.wenyan.content.gui.configMenu;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The Mod config file.
 */
public final class ModConfigFile {

    private ModConfigFile() {
        // Do nothing
    }

    private static FileType storedFileType;

    /**
     * Saves the config.
     */
    public final static Runnable saveRunnable = () -> {
        final Path path = Paths.get(storedFileType == FileType.JSON ? "config/betterf3.json" : "config/betterf3.toml");

        final File file = path.toFile();
        if (!file.exists()) file.getParentFile().mkdirs();

        final FileConfig config = FileConfig.builder(path).concurrent().autosave().build();

        final Config general = Config.inMemory();
        general.set("disable_mod", GeneralOptions.disableMod);
        general.set("auto_start", GeneralOptions.autoF3);
        general.set("space_modules", GeneralOptions.spaceEveryModule);
        general.set("shadow_text", GeneralOptions.shadowText);
        general.set("animations", GeneralOptions.enableAnimations);
        general.set("animationSpeed", GeneralOptions.animationSpeed);
        general.set("fontScale", GeneralOptions.fontScale);
        general.set("background_color", GeneralOptions.backgroundColor);
        general.set("hide_debug_crosshair", GeneralOptions.hideDebugCrosshair);
        general.set("hide_sidebar", GeneralOptions.hideSidebar);
        general.set("hide_bossbar", GeneralOptions.hideBossbar);
        general.set("always_show_profiler", GeneralOptions.alwaysEnableProfiler);
        general.set("always_show_tps", GeneralOptions.alwaysEnableTPS);
        general.set("always_show_ping", GeneralOptions.alwaysEnablePing);

        final List<Config> configsLeft = new ArrayList<>();

        for (final BaseModule module : BaseModule.modules) {

            final Config moduleConfig = saveModule(module);

            configsLeft.add(moduleConfig);

        }

        final List<Config> configsRight = new ArrayList<>();

        for (final BaseModule module : BaseModule.modulesRight) {

            final Config moduleConfig = saveModule(module);

            configsRight.add(moduleConfig);

        }

        config.set("modules_left", configsLeft);
        config.set("modules_right", configsRight);

        config.set("general", general);

        config.close();
    };

    /**
     * Loads the config.
     *
     * @param filetype the filetype (JSON or TOML)
     */
    public static void load(final FileType filetype) {

        storedFileType = filetype;

        final File file = new File(storedFileType == FileType.JSON ? "config/betterf3.json" : "config/betterf3.toml");

        if (!file.exists()) {
            return;
        }

        final FileConfig config = FileConfig.builder(file).concurrent().autosave().build();

        config.load();

        final Config allModulesConfig = config.getOrElse("modules", () -> null);

        // Support for old configs
        if (allModulesConfig != null) {

            for (final BaseModule module : BaseModule.allModules) {

                final String moduleName = module.id;

                final Config moduleConfig = allModulesConfig.getOrElse(moduleName, () -> null);

                if (moduleConfig == null) {
                    continue;
                }




                module.enabled = moduleConfig.getOrElse("enabled", true);

            }
        } else {
            // New config
            final List<BaseModule> modulesLeft = new ArrayList<>();
            final List<BaseModule> modulesRight = new ArrayList<>();

            final List<Config> modulesLeftConfig = config.getOrElse("modules_left", () -> null);

            if (modulesLeftConfig != null) {

                for (final Config moduleConfig : modulesLeftConfig) {
                    final String moduleName = moduleConfig.getOrElse("name", null);

                    if (moduleName == null) {
                        continue;
                    }

                    final BaseModule baseModule = ModConfigFile.loadModule(moduleConfig);

                    modulesLeft.add(baseModule);
                }
            }

            final List<Config> modulesRightConfig = config.getOrElse("modules_right", () -> null);

            if (modulesRightConfig != null) {
                for (final Config moduleConfig : modulesRightConfig) {

                    final String moduleName = moduleConfig.getOrElse("name", () -> null);

                    if (moduleName == null) {
                        continue;
                    }

                    final BaseModule baseModule = ModConfigFile.loadModule(moduleConfig);

                    modulesRight.add(baseModule);
                }
            }

            if (!modulesLeft.isEmpty() || !modulesRight.isEmpty()) {
                BaseModule.modules = modulesLeft;
                BaseModule.modulesRight = modulesRight;
            }

        }

        final Config general = config.getOrElse("general", () -> null);

        if (general != null) {

            if (allModulesConfig != null) {
                final List<BaseModule> modulesLeft = new ArrayList<>();
                final List<BaseModule> modulesRight = new ArrayList<>();

                for (final Object s : general.getOrElse("modules_left_order", new ArrayList<>())) {
                    final BaseModule baseModule = BaseModule.moduleById(s.toString());
                    if (baseModule != null) {
                        modulesLeft.add(baseModule);
                    }
                }

                if (!modulesLeft.isEmpty()) {
                    BaseModule.modules = modulesLeft;
                }

                for (final Object s : general.getOrElse("modules_right_order", new ArrayList<>())) {
                    final BaseModule baseModule = BaseModule.moduleById(s.toString());
                    if (baseModule != null) {
                        modulesRight.add(baseModule);
                    }
                }

                if (!modulesRight.isEmpty()) {
                    BaseModule.modulesRight = modulesRight;
                }
            }

            GeneralOptions.disableMod = general.getOrElse("disable_mod", false);
            GeneralOptions.autoF3 = general.getOrElse("auto_start", false);
            GeneralOptions.spaceEveryModule = general.getOrElse("space_modules", false);
            GeneralOptions.shadowText = general.getOrElse("shadow_text", true);
            GeneralOptions.enableAnimations = general.getOrElse("animations", true);
            GeneralOptions.animationSpeed = general.getOrElse("animationSpeed", 1.0);
            GeneralOptions.fontScale = general.getOrElse("fontScale", 1.0);
            GeneralOptions.backgroundColor = general.getOrElse("background_color", 0x6F505050);
            GeneralOptions.hideDebugCrosshair = general.getOrElse("hide_debug_crosshair", false);
            GeneralOptions.hideSidebar = general.getOrElse("hide_sidebar", true);
            GeneralOptions.hideBossbar = general.getOrElse("hide_bossbar", true);
            GeneralOptions.alwaysEnableProfiler = general.getOrElse("always_show_profiler", false);
            GeneralOptions.alwaysEnableTPS = general.getOrElse("always_show_tps", false);
            GeneralOptions.alwaysEnablePing = general.getOrElse("always_show_ping", false);
        }

        config.close();

    }

    private static BaseModule loadModule(final Config moduleConfig) {
        final String moduleName = moduleConfig.getOrElse("name", null);

        BaseModule baseModule;
        try {
            baseModule = BaseModule.moduleById(moduleName).getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NullPointerException | NoSuchMethodException | InvocationTargetException e) {
            baseModule = new EmptyModule(false);
        }


        baseModule.enabled = moduleConfig.getOrElse("enabled", true);
        return baseModule;
    }

    private static Config saveModule(final BaseModule module) {
        final Config moduleConfig = Config.inMemory();
        final Config lines = Config.inMemory();


        moduleConfig.set("name", module.id);

        if (module.nameColor != null) {
            moduleConfig.set("name_color", module.nameColor.getValue());
        }
        if (module.valueColor != null) {
            moduleConfig.set("value_color", module.valueColor.getValue());
        }


        moduleConfig.set("enabled", module.enabled);
        moduleConfig.set("lines", lines);

        return moduleConfig;
    }

    /**
     * The enum File type.
     */
    public enum FileType {
        /**
         * Json file type.
         */
        JSON,
        /**
         * Toml file type.
         */
        TOML
    }

}
