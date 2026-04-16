package indi.wenyan.setup.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class RecipeUtilities {
    /**
     * 适用于各种 2 行配方
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2,
                                       Character symbol1, ItemLike item1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder
                .shaped(items, category, outputItem, count)
                .pattern("   ")
                .pattern(row1)
                .pattern(row2)
                .define(symbol1, item1)
                .define(symbol2, item2)
                .unlockedBy("has_" + itemName(item1), provider.publicHas(item1))
                .unlockedBy("has_" + itemName(item2), provider.publicHas(item2))
                .save(output);
    }
    /**
     * 适用于各种 3 行配方
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, ItemLike item1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder
                .shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, item1)
                .define(symbol2, item2)
                .unlockedBy("has_" + itemName(item1), provider.publicHas(item1))
                .unlockedBy("has_" + itemName(item2), provider.publicHas(item2))
                .save(output);
    }

    /** 从 ItemLike 的注册 ID 中提取路径部分，用于 unlockedBy 的 has_<id> 命名。 */
    private static String itemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }
}
