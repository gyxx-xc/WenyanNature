package indi.wenyan.setup.datagen.recipe.craft;

import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.datagen.recipe.RecipeUtilities;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BlockRecipes {

    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Blocks ===

        // crafting_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.CRAFTING_BLOCK_ITEM.get(), 1,
                "   ",
                " c ",
                "sss",
                'c', Items.CRAFTING_TABLE,
                's', Items.SMOOTH_STONE_SLAB);

        // pedestal_block
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.PEDESTAL_BLOCK_ITEM.get(), 1,
                "bbb",
                " s ",
                "sss",
                'b', ItemTags.WOODEN_SLABS,
                's', Items.STICK);

        // writing_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.WRITING_BLOCK_ITEM.get(), 1,
                "p",
                "m",
                'p', Items.WRITABLE_BOOK,
                'm', Items.SMITHING_TABLE);

        // logic_furnace_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.LOGIC_FURNACE_BLOCK_ITEM.get(), 1,
                " r ",
                "zfz",
                " z ",
                'r', ItemTags.LIGHTNING_RODS,
                'z', WyRegistration.HAND_RUNNER_ITEM,
                'f', Items.FURNACE);

        // power_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.POWER_BLOCK_ITEM.get(), 1,
                " p ",
                "psp",
                " p ",
                'p', WenyanItems.DRAGON_PAPER,
                's', Items.NETHER_STAR);

        // formation_core_module_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM.get(), 1,
                " p ",
                "ptp",
                " p ",
                'p', WenyanItems.PHOENIX_PAPER,
                't', Items.TARGET);

        // screen_module_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get(), 1,
                " p ",
                "pgp",
                " p ",
                'p', WenyanItems.CLOUD_PAPER,
                'g', Items.GLOWSTONE);

        // lock_module_block_item
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.LOCK_MODULE_BLOCK_ITEM.get(), 1,
                " p ",
                "prp",
                " p ",
                'p', WenyanItems.FROST_PAPER,
                'r', Items.COMPARATOR);
    }
}
