package indi.wenyan.setup.datagen;

import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
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
    }
}
