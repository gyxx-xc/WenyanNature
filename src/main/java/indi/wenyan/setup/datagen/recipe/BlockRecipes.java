package indi.wenyan.setup.datagen.recipe;

import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BlockRecipes {

    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Blocks ===

        // crafting_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.CRAFTING_BLOCK_ITEM.get())
                .pattern("   ")
                .pattern(" c ")
                .pattern("sss")
                .define('c', Items.CRAFTING_TABLE)
                .define('s', Items.SMOOTH_STONE_SLAB)
                .unlockedBy("has_crafting_table", provider.publicHas(Items.CRAFTING_TABLE))
                .unlockedBy("has_smooth_stone_slab", provider.publicHas(Items.SMOOTH_STONE_SLAB))
                .save(output);

        // pedestal_block
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.PEDESTAL_BLOCK_ITEM.get())
                .pattern("bbb")
                .pattern(" s ")
                .pattern("sss")

                .define('b', ItemTags.WOODEN_SLABS)
                .define('s', Items.STICK)
                .unlockedBy("has_wooden_slabs", provider.publicHas(ItemTags.WOODEN_SLABS))
                .unlockedBy("has_stick", provider.publicHas(Items.STICK))
                .save(output);

        // writing_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.WRITING_BLOCK_ITEM.get())
                .pattern(" ")
                .pattern("p")
                .pattern("m")
                .define('p', Items.WRITABLE_BOOK)
                .define('m', Items.SMITHING_TABLE)
                .unlockedBy("has_writable_book", provider.publicHas(Items.WRITABLE_BOOK))
                .unlockedBy("has_smithing_table", provider.publicHas(Items.SMITHING_TABLE))
                .save(output);

        // logic_furnace_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.LOGIC_FURNACE_BLOCK_ITEM.get())
                .pattern(" r ")
                .pattern("zfz")
                .pattern(" z ")
                .define('r', ItemTags.LIGHTNING_RODS)
                .define('z', WyRegistration.HAND_RUNNER_ITEM)
                .define('f', Items.FURNACE)
                .unlockedBy("has_lightning_rods", provider.publicHas(ItemTags.LIGHTNING_RODS))
                .unlockedBy("has_hand_runner", provider.publicHas(WyRegistration.HAND_RUNNER_ITEM))
                .unlockedBy("has_furnace", provider.publicHas(Items.FURNACE))
                .save(output);

        // power_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.POWER_BLOCK_ITEM.get())
                .pattern(" p ")
                .pattern("psp")
                .pattern(" p ")
                .define('p', WenyanItems.DRAGON_PAPER)
                .define('s', Items.NETHER_STAR)
                .unlockedBy("has_dragon_paper", provider.publicHas(WenyanItems.DRAGON_PAPER))
                .unlockedBy("has_nether_star", provider.publicHas(Items.NETHER_STAR))
                .save(output);

        // formation_core_module_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM.get())
                .pattern(" p ")
                .pattern("ptp")
                .pattern(" p ")
                .define('p', WenyanItems.PHOENIX_PAPER)
                .define('t', Items.TARGET)
                .unlockedBy("has_target", provider.publicHas(Items.TARGET))
                .unlockedBy("has_phoenix_paper", provider.publicHas(WenyanItems.PHOENIX_PAPER))
                .save(output);
        // screen_module_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get())
                .pattern(" p ")
                .pattern("pgp")
                .pattern(" p ")
                .define('p', WenyanItems.CLOUD_PAPER)
                .define('g', Items.GLOWSTONE)
                .unlockedBy("has_cloud_paper", provider.publicHas(WenyanItems.CLOUD_PAPER))
                .unlockedBy("has_glowstone", provider.publicHas(Items.GLOWSTONE))
                .save(output);
        // lock_module_block_item
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.LOCK_MODULE_BLOCK_ITEM.get())
                .pattern(" p ")
                .pattern("prp")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER)
                .define('r', Items.COMPARATOR)
                .unlockedBy("has_frost_paper", provider.publicHas(WenyanItems.FROST_PAPER))
                .unlockedBy("has_comparator", provider.publicHas(Items.COMPARATOR))
                .save(output);
    }
}
