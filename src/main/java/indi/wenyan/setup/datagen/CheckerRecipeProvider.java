package indi.wenyan.setup.datagen;

import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.content.recipe.AnsweringRecipeInput;
import indi.wenyan.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Provider for generating checker recipes during data generation.
 * Defines various answering recipes that use checker system.
 */
public class CheckerRecipeProvider extends RecipeProvider {

    /**
     * Constructs a new checker recipe provider.
     * @param output The pack output for recipe generation
     * @param registries Future providing registry lookups
     */
    public CheckerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        AnsweringRecipeBuilder
                .create(Registration.HAND_RUNNER_1.get())
                .addInput(Items.PAPER)
                .question(CheckerFactory.PLUS_CHECKER)
                .unlock(has(Registration.HAND_RUNNER.get()))
                .save(recipeOutput, "hand_runner_1");
        AnsweringRecipeBuilder
                .create(Items.DIAMOND)
                .addInput(Items.COAL, 2)
                .question(CheckerFactory.LABYRINTH_CHECKER)
                .unlock(has(Items.DIAMOND))
                .save(recipeOutput, "diamond_labyrinth_checker");

        ShapedRecipeBuilder //Hand Runner 0
                .shaped(RecipeCategory.MISC,Registration.HAND_RUNNER.get())
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(recipeOutput);

        AnsweringRecipeBuilder //bamboo ink
                .create(Registration.BAMBOO_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.HAND_RUNNER_1.get()))
                .save(recipeOutput, "hand_runner_2");
        AnsweringRecipeBuilder //bamboo paper
                .create(Registration.BAMBOO_PAPER.get())
                .addInput(Items.PAPER, 1)
                .addInput(Items.BAMBOO,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.BAMBOO_PAPER.get()))
                .save(recipeOutput, "bamboo_ink");
        AnsweringRecipeBuilder //Hand Runner 1
                .create(Registration.HAND_RUNNER_1.get())
                .addInput(Registration.BAMBOO_INK.get(), 1)
                .addInput(Registration.BAMBOO_PAPER.get(), 1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.HAND_RUNNER_1.get()))
                .save(recipeOutput, "hand_runner_1");

        AnsweringRecipeBuilder //Cinnabar Ink
                .create(Registration.CINNABAR_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .addInput(Items.REDSTONE,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.HAND_RUNNER.get()))
                .save(recipeOutput, "cinnabar_ink");
        AnsweringRecipeBuilder //Cloud Paper
                .create(Registration.CLOUD_PAPER.get())
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.CLOUD_PAPER.get()))
                .save(recipeOutput, "cloud_paper");
        AnsweringRecipeBuilder //Hand Runner 2
                .create(Registration.HAND_RUNNER_2.get())
                .addInput(Registration.CINNABAR_INK.get(), 1)
                .addInput(Registration.CLOUD_PAPER.get(), 1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.HAND_RUNNER_1.get()))
                .save(recipeOutput, "hand_runner_2");

        AnsweringRecipeBuilder
                .create(Registration.STARLIGHT_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .addInput(Items.GLOWSTONE_DUST,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.HAND_RUNNER.get()))
                .save(recipeOutput, "starlight_ink");
        AnsweringRecipeBuilder
                .create(Registration.STAR_PAPER.get())
                .addInput(Items.PAPER, 1)
                .addInput(Items.GLOWSTONE_DUST,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(Registration.STAR_PAPER.get()))
                .save(recipeOutput, "starlight_paper");
    }
}
