package indi.wenyan.setup.datagen.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

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

        @Override
        protected void buildRecipes() {
                AnsweringRecipeBuilder
                                .create(WenyanItems.HAND_RUNNER_0)
                                .addInput(Items.PAPER)
                                .question(CheckerFactory.PLUS_CHECKER)
                                .round(8)
                                .save(output, "hand_runner");
                AnsweringRecipeBuilder
                                .create(Items.DIAMOND)
                                .addInput(Items.COAL, 2)
                                .question(CheckerFactory.LABYRINTH_CHECKER)
                                .save(output, "diamond_labyrinth_checker");
                ShapelessRecipeBuilder
                                .shapeless(BuiltInRegistries.ITEM, RecipeCategory.MISC, WenyanItems.FLOAT_NOTE.get())
                                .requires(Items.NAME_TAG)
                                .requires(Items.PAPER, 5)
                                .unlockedBy("has_name_tag", has(Items.NAME_TAG))
                                .save(output);

                ShapedRecipeBuilder // Hand Runner 0
                                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, WenyanItems.HAND_RUNNER_0.get())
                                .pattern("III")
                                .pattern("I I")
                                .pattern("III")
                                .define('I', Items.PAPER)
                                .unlockedBy("has_paper", has(Items.PAPER))
                                .save(output);

                AnsweringRecipeBuilder // bamboo ink
                                .create(WenyanItems.BAMBOO_INK)
                                .addInput(Items.POTION, 1)
                                .addInput(Items.BLACK_DYE, 1)
                                .question(CheckerFactory.PRINT_CHECKER)
                                .save(output, "bamboo_ink");
                AnsweringRecipeBuilder // bamboo paper
                                .create(WenyanItems.BAMBOO_PAPER)
                                .addInput(Items.PAPER, 1)
                                .addInput(Items.BAMBOO, 1)
                                .question(CheckerFactory.PLUS_CHECKER)
                                .save(output, "bamboo_paper");
                AnsweringRecipeBuilder // Hand Runner 1
                                .create(WenyanItems.HAND_RUNNER_1)
                                .addInput(WenyanItems.BAMBOO_INK.get(), 1)
                                .addInput(WenyanItems.BAMBOO_PAPER.get(), 1)
                                .question(CheckerFactory.HAND_RUNNER_1_CHECKER)
                                .save(output, "hand_runner_1");

                AnsweringRecipeBuilder // Cinnabar Ink
                                .create(WenyanItems.CINNABAR_INK)
                                .addInput(Items.POTION, 1)
                                .addInput(Items.BLACK_DYE, 1)
                                .addInput(Items.REDSTONE, 1)
                                .question(CheckerFactory.CINNABAR_INK_CHECKER)
                                .save(output, "cinnabar_ink");
                AnsweringRecipeBuilder // Cloud Paper
                                .create(WenyanItems.CLOUD_PAPER)
                                .addInput(Items.FEATHER, 1)
                                .addInput(Items.PAPER, 1)
                                .question(CheckerFactory.CLOUD_PAPER_CHECKER)
                                .save(output, "cloud_paper");
                AnsweringRecipeBuilder // Hand Runner 2
                                .create(WenyanItems.HAND_RUNNER_2)
                                .addInput(WenyanItems.CINNABAR_INK.get(), 1)
                                .addInput(WenyanItems.CLOUD_PAPER.get(), 1)
                                .question(CheckerFactory.HAND_RUNNER_2_CHECKER)
                                .save(output, "hand_runner_2");

                AnsweringRecipeBuilder
                                .create(WenyanItems.STARLIGHT_INK)
                                .addInput(Items.POTION, 1)
                                .addInput(Items.BLACK_DYE, 1)
                                .addInput(Items.GLOWSTONE_DUST, 1)
                                .question(CheckerFactory.STARLIGHT_INK_CHECKER)
                                .save(output, "starlight_ink");
                AnsweringRecipeBuilder
                                .create(WenyanItems.STAR_PAPER)
                                .addInput(Items.PAPER, 1)
                                .addInput(Items.GLOWSTONE_DUST, 1)
                                .question(CheckerFactory.PLUS_CHECKER) // Need change
                                .save(output, "star_paper");
                AnsweringRecipeBuilder // Hand Runner 3
                                .create(WenyanItems.HAND_RUNNER_3)
                                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                                .addInput(WenyanItems.STAR_PAPER.get(), 1)
                                .question(CheckerFactory.PLUS_CHECKER) // Need change
                                .save(output, "hand_runner_3");

                AnsweringRecipeBuilder // Lunar ink
                                .create(WenyanItems.LUNAR_INK)
                                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                                .addInput(Items.BLACK_DYE, 1)
                                .question(CheckerFactory.PLUS_CHECKER) // Need Change
                                .save(output, "lunar_ink");
                AnsweringRecipeBuilder // frost_paper
                                .create(WenyanItems.FROST_PAPER)
                                .addInput(Items.SNOWBALL, 1)
                                .addInput(Items.GOLD_NUGGET, 1)
                                .addInput(Items.PAPER, 1)
                                .question(CheckerFactory.PLUS_CHECKER)
                                .save(output, "frost_paper");
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
