package indi.wenyan.judou.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ConfigManager {
    ;

    @Nullable
    private static IConfigProvider provider = null;

    public static void registerConfigProvider(@NotNull IConfigProvider p) {
        if (provider != null)
            throw new IllegalStateException("Config already registered");
        provider = p;
    }

    public static IConfigProvider getConfig() {
        if (provider == null)
            throw new IllegalStateException("Config not registered");
        return provider;
    }
}
