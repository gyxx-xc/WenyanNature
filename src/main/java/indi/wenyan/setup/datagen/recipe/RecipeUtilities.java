package indi.wenyan.setup.datagen.recipe;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.recipe.answering.checker.CheckerEnum;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RecipeUtilities {

    /*
    ItemLike 方法优先：
        2 物品，2 行
        2 物品，3 行
        3 物品，3 行
        1 物品，3 行
    TagLike 方法随后：
        2 物品 (Tag + Item)，自动推导 Key
        2 物品 (Tag + Item)，手动指定 Key
        3 物品 (Tag + Item + Item)，自动推导 Key
        3 物品 (Tag + Item + Item)，手动指定 Key
        3 物品 (Tag + Tag + Item)，自动推导 Key
    Ingredient 方法随后：
        2 物品 (Ingredient + Item)，手动指定 Key
    无序配方 (Shapeless)：
        支持任意数量 ItemLike
    回答配方 (Answering)：
        支持自动或手动指定名称
     */
    /**
     * 适用于各种 2 物品, 2 行配方。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2,
                                       Character symbol1, ItemLike item1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = addItemUnlocks(provider, ShapedRecipeBuilder
                        .shaped(items, category, outputItem, count)
                        .pattern(row1)
                        .pattern(row2)
                        .define(symbol1, item1)
                        .define(symbol2, item2), item1, item2);
        builder
                .save(output);
    }

    /**
     * 适用于 2 物品, 3 行配方 (全为 ItemLike)。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, ItemLike item1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = addItemUnlocks(provider, ShapedRecipeBuilder
                        .shaped(items, category, outputItem, count)
                        .pattern(row1)
                        .pattern(row2)
                        .pattern(row3)
                        .define(symbol1, item1)
                        .define(symbol2, item2), item1, item2);
        builder
                .save(output);
    }

    /**
     * 适用于 2 物品, 3 行配方 (全为 ItemLike)，指定模组命名空间下的配方名称。
     */
    public static void newModShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                          HolderGetter<Item> items, RecipeCategory category, String recipeName,
                                          ItemLike outputItem, int count,
                                          String row1, String row2, String row3,
                                          Character symbol1, ItemLike item1,
                                          Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = addItemUnlocks(provider, ShapedRecipeBuilder
                        .shaped(items, category, outputItem, count)
                        .pattern(row1)
                        .pattern(row2)
                        .pattern(row3)
                        .define(symbol1, item1)
                        .define(symbol2, item2), item1, item2);
        builder
                .save(output, modRecipeKey(recipeName));
    }

    /**
     * 适用于 3 物品, 3 行配方 (全为 ItemLike)。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, ItemLike item1,
                                       Character symbol2, ItemLike item2,
                                       Character symbol3, ItemLike item3) {
        ShapedRecipeBuilder builder = addItemUnlocks(provider, ShapedRecipeBuilder
                        .shaped(items, category, outputItem, count)
                        .pattern(row1)
                        .pattern(row2)
                        .pattern(row3)
                        .define(symbol1, item1)
                        .define(symbol2, item2)
                        .define(symbol3, item3), item1, item2, item3);
        builder
                .save(output);
    }

    /**
     * 适用于 1 物品, 3 行配方 (全为 ItemLike)。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, ItemLike item1) {
        ShapedRecipeBuilder builder = addItemUnlocks(provider, ShapedRecipeBuilder
                        .shaped(items, category, outputItem, count)
                        .pattern(row1)
                        .pattern(row2)
                        .pattern(row3)
                        .define(symbol1, item1), item1);
        builder
                .save(output);
    }

    /**
     * 适用于 2 symbol 3 行配方，symbol1 为 ItemTag，自动推导 has_<tag> unlock key。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, TagKey<Item> tag1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, tag1)
                .define(symbol2, item2);
        addTagUnlock(provider, builder, tag1);
        addItemUnlocks(provider, builder, item2)
                .save(output);
    }

    /**
     * 适用于 2 symbol 3 行配方，symbol1 为 ItemTag，unlockKey1 由调用方手动提供。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, TagKey<Item> tag1, String unlockKey1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, tag1)
                .define(symbol2, item2);
        addTagUnlock(provider, builder, unlockKey1, tag1);
        addItemUnlocks(provider, builder, item2)
                .save(output);
    }

    /**
     * 适用于 3 symbol 3 行配方，symbol1 为 ItemTag，symbol2/3 为 ItemLike，自动推导 has_<tag> unlock key。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, TagKey<Item> tag1,
                                       Character symbol2, ItemLike item2,
                                       Character symbol3, ItemLike item3) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, tag1)
                .define(symbol2, item2)
                .define(symbol3, item3);
        addTagUnlock(provider, builder, tag1);
        addItemUnlocks(provider, builder, item2, item3)
                .save(output);
    }

    /**
     * 适用于 3 symbol 3 行配方，支持混合 Tag 和 ItemLike，unlockBy 由调用方手动提供。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, TagKey<Item> tag1, String unlockKey1,
                                       Character symbol2, ItemLike item2,
                                       Character symbol3, ItemLike item3) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, tag1)
                .define(symbol2, item2)
                .define(symbol3, item3);
        addTagUnlock(provider, builder, unlockKey1, tag1);
        addItemUnlocks(provider, builder, item2, item3)
                .save(output);
    }

    /**
     * 适用于 3 symbol 3 行配方，symbol1/2 为 ItemTag，symbol3 为 ItemLike，自动推导 has_<tag> unlock key。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, TagKey<Item> tag1,
                                       Character symbol2, TagKey<Item> tag2,
                                       Character symbol3, ItemLike item3) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, tag1)
                .define(symbol2, tag2)
                .define(symbol3, item3);
        addTagUnlocks(provider, builder, tag1, tag2);
        addItemUnlocks(provider, builder, item3)
                .save(output);
    }
    /**
     * 适用于 2 symbol 3 行配方，symbol1 为 Ingredient，symbol2 为 ItemLike。
     * 用于如 Throw Module 这类包含复杂组成的配方。
     */
    public static void newShapedRecipe(CheckerRecipeProvider provider, RecipeOutput output,
                                       HolderGetter<Item> items, RecipeCategory category,
                                       ItemLike outputItem, int count,
                                       String row1, String row2, String row3,
                                       Character symbol1, Ingredient ing1, ItemLike unlockItem1, String unlockKey1,
                                       Character symbol2, ItemLike item2) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, category, outputItem, count)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .define(symbol1, ing1)
                .define(symbol2, item2);
        addItemUnlock(provider, builder, unlockKey1, unlockItem1);
        addItemUnlocks(provider, builder, item2)
                .save(output);
    }

    /**
     * 适用于无序配方 (Shapeless)。
     */
    public static void newShapelessRecipe(CheckerRecipeProvider provider, HolderGetter<Item> items, RecipeOutput output,
                                          RecipeCategory category, ItemLike outputItem, int count,
                                          ItemLike... inputs) {
        var builder = ShapelessRecipeBuilder.shapeless(items, category, outputItem, count);
        addIngredientsAndUnlocks(provider, builder, inputs);
        builder.save(output);
    }

    /**
     * 适用于无序配方 (Shapeless)，指定配方名称。
     */
    public static void newShapelessRecipe(CheckerRecipeProvider provider, HolderGetter<Item> items, RecipeOutput output,
                                          RecipeCategory category, String recipeName,
                                          ItemLike outputItem, int count,
                                          ItemLike... inputs) {
        var builder = ShapelessRecipeBuilder.shapeless(items, category, outputItem, count);
        addIngredientsAndUnlocks(provider, builder, inputs);
        builder.save(output, recipeName);
    }

    /**
     * 适用于无序配方 (Shapeless)，指定模组命名空间下的配方名称。
     */
    public static void newModShapelessRecipe(CheckerRecipeProvider provider, HolderGetter<Item> items, RecipeOutput output,
                                             RecipeCategory category, String recipeName,
                                             ItemLike outputItem, int count,
                                             ItemLike... inputs) {
        var builder = ShapelessRecipeBuilder.shapeless(items, category, outputItem, count);
        addIngredientsAndUnlocks(provider, builder, inputs);
        builder.save(output, modRecipeKey(recipeName));
    }

    /**
     * 适用于 AnsweringRecipe，自动使用物品名称。
     */
    public static void newAnsweringRecipe(RecipeOutput output, ItemLike outputItem, int count,
                                          CheckerEnum question, int round,
                                          ItemLike... inputs) {
        var builder = AnsweringRecipeBuilder.create(outputItem.asItem(), count)
                .question(question)
                .round(round);
        for (ItemLike input : inputs) {
            builder.addInput(input.asItem());
        }
        builder.save(output, itemName(outputItem));
    }

    /**
     * 适用于 AnsweringRecipe，指定配方名称。
     */
    public static void newAnsweringRecipe(RecipeOutput output, String recipeName,
                                          ItemLike outputItem, int count,
                                          CheckerEnum question, int round,
                                          ItemLike... inputs) {
        var builder = AnsweringRecipeBuilder.create(outputItem.asItem(), count)
                .question(question)
                .round(round);
        for (ItemLike input : inputs) {
            builder.addInput(input.asItem());
        }
        builder.save(output, recipeName);
    }

    /** 从 ItemLike 的注册 ID 中提取路径部分，用于 unlockedBy 的 has_<id> 命名。 */
    private static String itemName(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private static ShapedRecipeBuilder addItemUnlocks(CheckerRecipeProvider provider,
                                                      ShapedRecipeBuilder builder,
                                                      ItemLike... items) {
        for (ItemLike item : items) {
            addItemUnlock(provider, builder, item);
        }
        return builder;
    }

    private static void addItemUnlock(CheckerRecipeProvider provider,
                                      ShapedRecipeBuilder builder,
                                      ItemLike item) {
        addItemUnlock(provider, builder, "has_" + itemName(item), item);
    }

    private static void addItemUnlock(CheckerRecipeProvider provider,
                                      ShapedRecipeBuilder builder,
                                      String key,
                                      ItemLike item) {
        builder.unlockedBy(key, provider.publicHas(item));
    }

    private static void addIngredientsAndUnlocks(CheckerRecipeProvider provider,
                                                ShapelessRecipeBuilder builder,
                                                ItemLike... inputs) {
        for (ItemLike input : inputs) {
            builder.requires(input);
            builder.unlockedBy("has_" + itemName(input), provider.publicHas(input));
        }
    }

    /** 从 TagKey 的 location 中提取路径部分，用于 unlockedBy 的 has_<tag> 命名。 */
    private static String tagName(TagKey<Item> tag) {
        return tag.location().getPath();
    }

    @SafeVarargs
    private static void addTagUnlocks(CheckerRecipeProvider provider,
                                      ShapedRecipeBuilder builder,
                                      TagKey<Item>... tags) {
        for (TagKey<Item> tag : tags) {
            addTagUnlock(provider, builder, tag);
        }
    }

    private static void addTagUnlock(CheckerRecipeProvider provider,
                                     ShapedRecipeBuilder builder,
                                     TagKey<Item> tag) {
        addTagUnlock(provider, builder, "has_" + tagName(tag), tag);
    }

    private static void addTagUnlock(CheckerRecipeProvider provider,
                                     ShapedRecipeBuilder builder,
                                     String key,
                                     TagKey<Item> tag) {
        builder.unlockedBy(key, provider.publicHas(tag));
    }

    private static ResourceKey<Recipe<?>> modRecipeKey(String recipeName) {
        return ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, recipeName));
    }
}
