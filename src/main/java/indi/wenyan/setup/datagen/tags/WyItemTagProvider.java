package indi.wenyan.setup.datagen.tags;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WyItemTagProvider extends ItemTagsProvider {

    public WyItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(WyRegistration.RUNNER_PAPER_ITEM).add(
                WenyanItems.BAMBOO_PAPER.get(),
                WenyanItems.CLOUD_PAPER.get(),
                WenyanItems.FROST_PAPER.get(),
                WenyanItems.PHOENIX_PAPER.get(),
                WenyanItems.STARLIGHT_PAPER.get(),
                WenyanItems.DRAGON_PAPER.get());

        tag(WyRegistration.COBBLESTONES_ITEM).add(
                Items.COBBLESTONE,
                Items.ANDESITE,
                Items.DIORITE,
                Items.GRANITE,
                Items.BLACKSTONE,
                Items.COBBLED_DEEPSLATE);

        tag(WyRegistration.STONE_BRICKS_ITEM).add(
                Items.STONE_BRICKS,
                Items.DEEPSLATE_BRICKS);

        tag(WyRegistration.PAPER_MODULE_ITEM).add(
                WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.MATH_MODULE_BLOCK_ITEM.get(),
                WenyanItems.BIT_MODULE_BLOCK_ITEM.get(),
                WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get(),
                WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get(),
                WenyanItems.ITEM_MODULE_BLOCK_ITEM.get(),
                WenyanItems.VEC3_MODULE_BLOCK_ITEM.get(),
                WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get(),
                WenyanItems.STRING_MODULE_BLOCK_ITEM.get(),
                WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get(),
                WenyanItems.PISTON_MODULE_BLOCK_ITEM.get());
    }
}
