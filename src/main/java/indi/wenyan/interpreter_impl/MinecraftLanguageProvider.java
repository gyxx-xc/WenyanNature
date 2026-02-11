package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.utils.ILanguageProvider;
import net.minecraft.network.chat.Component;

public class MinecraftLanguageProvider implements ILanguageProvider {
    @Override
    public String getTranslation(String key) {
        return Component.translatable(key).getString();
    }
}
