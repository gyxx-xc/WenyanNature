package indi.wenyan.content.item;

import indi.wenyan.content.block.runner.ICodeHolder;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public enum ItemCodeHolder {
    ;

    public static ICodeHolder getCodeCapability(ItemStack item) {
        return new ICodeHolder() {
            @Override
            public void setCode(String content) {
                item.set(WyRegistration.PROGRAM_CODE_DATA.get(), content);
            }

            @Override
            public String getCode() {
                return item.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            }

            @Override
            public void setPlatformName(String platformName) {
                item.set(DataComponents.CUSTOM_NAME, Component.literal(platformName));
            }

            @Override
            public String getPlatformName() {
                return item.getOrDefault(DataComponents.CUSTOM_NAME, Component.translatable("code.wenyan_programming.bracket", item.getItemName())).getString();
            }
        };
    }
}
