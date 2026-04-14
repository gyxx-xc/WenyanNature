package indi.wenyan.setup.datagen.recipe.craft;

import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModuleRecipes {
    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Tool and Module ===
        // 印符 (print_inventory_module) - 无序
        ShapelessRecipeBuilder
                .shapeless(items, RecipeCategory.MISC,
                        WenyanItems.PRINT_INVENTORY_MODULE, 2)
                .requires(Items.PAPER)
                .requires(Ingredient.of(Items.COAL, Items.CHARCOAL))
                .requires(Items.FEATHER)
                .unlockedBy("has_paper", provider.publicHas(Items.PAPER)).save(output);

        // 位元符 (bit_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.BIT_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.REDSTONE)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_redstone", provider.publicHas(Items.REDSTONE))
                .save(output);

        // 数符 (math_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.MATH_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.COPPER_INGOT)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_copper_ingot", provider.publicHas(Items.COPPER_INGOT))
                .save(output);

        // 向量符 (vec3_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.VEC3_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.IRON_INGOT)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_iron_ingot", provider.publicHas(Items.IRON_INGOT))
                .save(output);

        // 熵符 (random_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.RANDOM_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.ROTTEN_FLESH)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_rotten_flesh", provider.publicHas(Items.ROTTEN_FLESH))
                .save(output);

        // 字串符 (string_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.STRING_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.STRING)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_string", provider.publicHas(Items.STRING))
                .save(output);

        // 集符 (collection_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.COLLECTION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.BOWL)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_bowl", provider.publicHas(Items.BOWL))
                .save(output);

        // 物品符 (item_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.ITEM_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.CLOUD_PAPER.get())
                .define('i', Items.CHEST)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.CLOUD_PAPER.get()))
                .unlockedBy("has_chest", provider.publicHas(Items.CHEST))
                .save(output);

        // 方块符 (block_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.BLOCK_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.CLOUD_PAPER.get())
                .define('i', Items.SMOOTH_STONE)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.CLOUD_PAPER.get()))
                .unlockedBy("has_smooth_stone", provider.publicHas(Items.SMOOTH_STONE))
                .save(output);

        // 实体符 (entity_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.ENTITY_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.STARLIGHT_PAPER.get())
                .define('i', Items.EGG)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.STARLIGHT_PAPER.get()))
                .unlockedBy("has_egg", provider.publicHas(Items.EGG))
                .save(output);

        // 天下情报符 (information_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.INFORMATION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.COMPASS)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.FROST_PAPER.get()))
                .unlockedBy("has_compass", provider.publicHas(Items.COMPASS))
                .save(output);

        // 爆裂符 (explosion_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.FIRE_CHARGE)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.FROST_PAPER.get()))
                .unlockedBy("has_fire_charge", provider.publicHas(Items.FIRE_CHARGE))
                .save(output);

        // 阻塞队列符 (blocking_queue_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.STARLIGHT_PAPER.get())
                .define('i', Items.HOPPER)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.STARLIGHT_PAPER.get()))
                .unlockedBy("has_hopper", provider.publicHas(Items.HOPPER))
                .save(output);

        // 活塞符 (piston_module_block)
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.PISTON_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.STICKY_PISTON)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.FROST_PAPER.get()))
                .unlockedBy("has_sticky_piston", provider.publicHas(Items.STICKY_PISTON))
                .save(output);

        // 通讯符 (communicate_module_block)


    }
}
