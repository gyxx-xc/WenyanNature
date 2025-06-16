package indi.wenyan.setup.datagen;

import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CheckerRecipeProvider extends RecipeProvider {
    public CheckerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        AnsweringRecipeBuilder
                .create(Registration.HAND_RUNNER_1.get())
                .addInput(Items.PAPER)
                .question(CheckerFactory.ECHO_CHECKER)
                .unlock(has(Registration.HAND_RUNNER.get()))
                .save(recipeOutput, "hand_runner_1");
    }
}
