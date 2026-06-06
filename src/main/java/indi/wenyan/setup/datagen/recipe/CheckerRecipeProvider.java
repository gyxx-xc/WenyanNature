package indi.wenyan.setup.datagen.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.datagen.recipe.crafting.CraftingHandrunnerRecipes;
import indi.wenyan.setup.datagen.recipe.wyquestion.BlockRecipes;
import indi.wenyan.setup.datagen.recipe.wyquestion.HandrunnerRecipes;
import indi.wenyan.setup.datagen.recipe.wyquestion.ItemRecipes;
import indi.wenyan.setup.datagen.recipe.wyquestion.ModuleRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

/**
 * Provider for generating checker recipes during data generation.
 * Defines various answering recipes that use checker system.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CheckerRecipeProvider extends RecipeProvider {

    /**
     * Constructs a new checker recipe provider.
     *
     * @param output   The pack output for recipe generation
     * @param provider Future providing registry lookups
     */
    public CheckerRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    public <T> T publicHas(ItemLike item) {
        return (T) this.has(item);
    }

    public <T> T publicHas(TagKey<Item> tag) {
        return (T) this.has(tag);
    }

    @Override
    protected void buildRecipes() {
        HandrunnerRecipes.build(this.items, output, this);
        CraftingHandrunnerRecipes.build(this.items, output, this);
        ModuleRecipes.build(this.items, output, this);
        ItemRecipes.build(items, output, this);
        BlockRecipes.build(this.items, output, this);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider,
                RecipeOutput recipeOutput) {
            return new CheckerRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Wenyan Recipes";
        }
    }
}
