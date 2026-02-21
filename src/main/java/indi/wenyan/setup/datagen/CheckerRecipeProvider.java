package indi.wenyan.setup.datagen;

import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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
                .create(WenyanItems.HAND_RUNNER_0.get())
                .addInput(Items.PAPER)
                .question(CheckerFactory.PLUS_CHECKER)
                .round(8)
                .unlock(has(WenyanItems.HAND_RUNNER_0.get()))
                .save(recipeOutput, "hand_runner");
        AnsweringRecipeBuilder
                .create(Items.DIAMOND)
                .addInput(Items.COAL, 2)
                .question(CheckerFactory.LABYRINTH_CHECKER)
                .unlock(has(Items.DIAMOND))
                .save(recipeOutput, "diamond_labyrinth_checker");
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, WenyanItems.FLOAT_NOTE.get())
                .requires(Items.NAME_TAG)
                .requires(Items.PAPER, 5)
                .unlockedBy("has_name_tag", has(Items.NAME_TAG))
                .save(recipeOutput);

        ShapedRecipeBuilder //Hand Runner 0
                .shaped(RecipeCategory.MISC, WenyanItems.HAND_RUNNER_0.get())
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(recipeOutput);

        AnsweringRecipeBuilder //bamboo ink
                .create(WenyanItems.BAMBOO_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_1.get()))
                .save(recipeOutput, "bamboo_ink");
        AnsweringRecipeBuilder //bamboo paper
                .create(WenyanItems.BAMBOO_PAPER.get())
                .addInput(Items.PAPER, 1)
                .addInput(Items.BAMBOO,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.BAMBOO_PAPER.get()))
                .save(recipeOutput, "bamboo_paper");
        AnsweringRecipeBuilder //Hand Runner 1
                .create(WenyanItems.HAND_RUNNER_1.get())
                .addInput(WenyanItems.BAMBOO_INK.get(), 1)
                .addInput(WenyanItems.BAMBOO_PAPER.get(), 1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_1.get()))
                .save(recipeOutput, "hand_runner_1");

        AnsweringRecipeBuilder //Cinnabar Ink
                .create(WenyanItems.CINNABAR_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .addInput(Items.REDSTONE,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_0.get()))
                .save(recipeOutput, "cinnabar_ink");
        AnsweringRecipeBuilder //Cloud Paper
                .create(WenyanItems.CLOUD_PAPER.get())
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.CLOUD_PAPER.get()))
                .save(recipeOutput, "cloud_paper");
        AnsweringRecipeBuilder //Hand Runner 2
                .create(WenyanItems.HAND_RUNNER_2.get())
                .addInput(WenyanItems.CINNABAR_INK.get(), 1)
                .addInput(WenyanItems.CLOUD_PAPER.get(), 1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_1.get()))
                .save(recipeOutput, "hand_runner_2");

        AnsweringRecipeBuilder
                .create(WenyanItems.STARLIGHT_INK.get())
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE,1)
                .addInput(Items.GLOWSTONE_DUST,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_0.get()))
                .save(recipeOutput, "starlight_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.STAR_PAPER.get())
                .addInput(Items.PAPER, 1)
                .addInput(Items.GLOWSTONE_DUST,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.STAR_PAPER.get()))
                .save(recipeOutput, "star_paper");
        AnsweringRecipeBuilder //Hand Runner 3
                .create(WenyanItems.HAND_RUNNER_3.get())
                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                .addInput(WenyanItems.STAR_PAPER.get(), 1)
                .question(CheckerFactory.PLUS_CHECKER) //Need change
                .unlock(has(WenyanItems.HAND_RUNNER_2.get()))
                .save(recipeOutput, "hand_runner_3");

        AnsweringRecipeBuilder //Lunar ink
                .create(WenyanItems.LUNAR_INK.get())
                .addInput(WenyanItems.STARLIGHT_INK.get(),1)
                .addInput(Items.BLACK_DYE,1)
                .question(CheckerFactory.PLUS_CHECKER) //Need Change
                .unlock(has(WenyanItems.HAND_RUNNER_2.get()))
                .save(recipeOutput,"lunar_ink");
        AnsweringRecipeBuilder //frost_paper
                .create(WenyanItems.FROST_PAPER.get())
                .addInput(Items.SNOWBALL,1)
                .addInput(Items.GOLD_NUGGET,1)
                .addInput(Items.PAPER,1)
                .question(CheckerFactory.PLUS_CHECKER)
                .unlock(has(WenyanItems.HAND_RUNNER_3.get()))
                .save(recipeOutput,"frost_paper");
    }
}
