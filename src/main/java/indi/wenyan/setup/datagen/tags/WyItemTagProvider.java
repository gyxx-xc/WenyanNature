package indi.wenyan.setup.datagen.tags;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WyItemTagProvider extends ItemTagsProvider {
    public WyItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(WyRegistration.MODULE_ITEM).add(
                WenyanItems.CRAFTING_BLOCK_ITEM.get(),
                WenyanItems.PEDESTAL_BLOCK_ITEM.get(),
                WenyanItems.WRITING_BLOCK_ITEM.get(),
                WenyanItems.POWER_BLOCK_ITEM.get(),
                WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.MATH_MODULE_BLOCK_ITEM.get(),
                WenyanItems.BIT_MODULE_BLOCK_ITEM.get(),
                WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get(),
                WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get(),
                WenyanItems.ITEM_MODULE_BLOCK_ITEM.get(),
                WenyanItems.VEC3_MODULE_BLOCK_ITEM.get(),
                WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM.get(),
                WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.STRING_MODULE_BLOCK_ITEM.get(),
                WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get(),
                WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get(),
                WenyanItems.LOCK_MODULE_BLOCK_ITEM.get(),
                WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM.get(),
                WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM.get(),
                WenyanItems.PISTON_MODULE_BLOCK_ITEM.get()
        );
    }
}
