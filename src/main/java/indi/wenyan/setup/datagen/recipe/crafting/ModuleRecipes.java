package indi.wenyan.setup.datagen.recipe.crafting;

import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.datagen.recipe.RecipeUtilities;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModuleRecipes {
    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Tool and Module ===
        // 位元符 (bit_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.BIT_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.REDSTONE);

        // 数符 (math_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.MATH_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.COPPER_INGOT);

        // 向量符 (vec3_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.VEC3_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.IRON_INGOT);

        // 熵符 (random_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.RANDOM_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.ROTTEN_FLESH);

        // 字串符 (string_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.STRING_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.STRING);

        // 集符 (collection_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.COLLECTION_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.BAMBOO_PAPER.get(),
                'i', Items.BOWL);

        // 物品符 (item_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.ITEM_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.CLOUD_PAPER.get(),
                'i', Items.CHEST);

        // 方块符 (block_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.BLOCK_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.CLOUD_PAPER.get(),
                'i', Items.SMOOTH_STONE);

        // 实体符 (entity_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.ENTITY_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.STARLIGHT_PAPER.get(),
                'i', Items.EGG);

        // 天下情报符 (information_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.INFORMATION_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.FROST_PAPER.get(),
                'i', Items.COMPASS);

        // 爆裂符 (explosion_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.FROST_PAPER.get(),
                'i', Items.FIRE_CHARGE);

        // 阻塞队列符 (blocking_queue_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.STARLIGHT_PAPER.get(),
                'i', Items.HOPPER);

        // 活塞符 (piston_module_block)
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.PISTON_MODULE_BLOCK_ITEM, 2,
                " p ",
                " i ",
                " p ",
                'p', WenyanItems.FROST_PAPER.get(),
                'i', Items.STICKY_PISTON);

        // 通讯符 (communicate_module_block)


    }
}
