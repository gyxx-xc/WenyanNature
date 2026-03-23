package indi.wenyan.setup.language;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public interface ILocalizationEnum {

    static Component getName(Item item) {
        return item.getName(item.getDefaultInstance());
    }

    String getTranslationKey();

    default String string() {
        return text().getString();
    }

    default String string(Object... args) {
        return text(args).getString();
    }

    default MutableComponent text() {
        return Component.translatable(getTranslationKey());
    }

    default MutableComponent text(Object... args) {
        return Component.translatable(getTranslationKey(), args);
    }
}
