package indi.wenyan.setup.datagen.recipe.wyquestion;

import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.datagen.recipe.RecipeUtilities;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemRecipes {

    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        // === Items ===
        // float_note
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.FLOAT_NOTE.get(), 1,
                "is ",
                "is ",
                'i', Items.STRING,
                's', Items.STICK);
    }
}
