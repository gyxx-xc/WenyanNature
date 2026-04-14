package indi.wenyan.setup.datagen.recipe;

import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRecipes {

    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Items ===
        // float_note
        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC, WenyanItems.FLOAT_NOTE.get())
                .pattern("is ")
                .pattern("is ")
                .define('i', Items.STRING)
                .define('s', Items.STICK)
                .unlockedBy("has_string", provider.publicHas(Items.STRING))
                .unlockedBy("has_stick", provider.publicHas(Items.STICK))
                .save(output);
    }
}
