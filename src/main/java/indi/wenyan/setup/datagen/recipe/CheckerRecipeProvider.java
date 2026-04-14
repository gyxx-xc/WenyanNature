package indi.wenyan.setup.datagen.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.checker.CheckerEnum;
import indi.wenyan.content.recipe.combine_module.ThrowModuleRecipe;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

import static indi.wenyan.setup.definitions.WenyanItems.THROW_RUNNER;

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

    public <T> T publicHas(net.minecraft.world.level.ItemLike item) {
        return (T) this.has(item);
    }

    public <T> T publicHas(TagKey<Item> tag) {
        return (T) this.has(tag);
    }

    @Override
    protected void buildRecipes() {

        HandrunnerRecipes.build(this.items, output, this);
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
