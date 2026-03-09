package indi.wenyan.setup.datagen.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.checker.CheckerFactory;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.BlockItem;
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
        // recipe that clear code in runner
        clearDataRecipe(WenyanItems.HAND_RUNNER_0.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_1.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_2.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_3.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_4.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_5.get());
        clearDataRecipe(WenyanItems.HAND_RUNNER_6.get());

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
                .create(WenyanItems.BAMBOO_INK, 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .question(CheckerFactory.PRINT_CHECKER)
                .save(output, "bamboo_ink");
        AnsweringRecipeBuilder // bamboo paper
                .create(WenyanItems.BAMBOO_PAPER, 4)
                .addInput(Items.PAPER, 1)
                .addInput(Items.BAMBOO, 1)
                .question(CheckerFactory.PLUS_CHECKER)
                .save(output, "bamboo_paper");
        AnsweringRecipeBuilder // Hand Runner 1
                .create(WenyanItems.HAND_RUNNER_1, 2)
                .addInput(WenyanItems.BAMBOO_INK.get(), 1)
                .addInput(WenyanItems.BAMBOO_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_1_CHECKER)
                .save(output, "hand_runner_1");

        AnsweringRecipeBuilder // Cinnabar Ink
                .create(WenyanItems.CINNABAR_INK, 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .addInput(Items.REDSTONE, 1)
                .question(CheckerFactory.CINNABAR_INK_CHECKER)
                .save(output, "cinnabar_ink");
        AnsweringRecipeBuilder // Cloud Paper
                .create(WenyanItems.CLOUD_PAPER, 4)
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerFactory.CLOUD_PAPER_CHECKER)
                .save(output, "cloud_paper");
        AnsweringRecipeBuilder // Hand Runner 2
                .create(WenyanItems.HAND_RUNNER_2, 2)
                .addInput(WenyanItems.CINNABAR_INK.get(), 1)
                .addInput(WenyanItems.CLOUD_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_2_CHECKER)
                .save(output, "hand_runner_2");

        AnsweringRecipeBuilder
                .create(WenyanItems.STARLIGHT_INK, 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .addInput(Items.GLOWSTONE_DUST, 1)
                .question(CheckerFactory.STARLIGHT_INK_CHECKER)
                .save(output, "starlight_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.STARLIGHT_PAPER, 4)
                .addInput(Items.PAPER, 1)
                .addInput(Items.GLOWSTONE_DUST, 1)
                .question(CheckerFactory.STARLIGHT_PAPER_CHECKER) // Need change
                .save(output, "star_paper");
        AnsweringRecipeBuilder // Hand Runner 3
                .create(WenyanItems.HAND_RUNNER_3, 2)
                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                .addInput(WenyanItems.STARLIGHT_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_3_CHECKER) // Need change
                .save(output, "hand_runner_3");

        AnsweringRecipeBuilder // Lunar ink
                .create(WenyanItems.LUNAR_INK, 4)
                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                .addInput(Items.BLACK_DYE, 1)
                .question(CheckerFactory.LUNAR_INK_CHECKER)
                .save(output, "lunar_ink");
        AnsweringRecipeBuilder // frost_paper
                .create(WenyanItems.FROST_PAPER, 4)
                .addInput(Items.SNOWBALL, 1)
                .addInput(Items.GOLD_NUGGET, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerFactory.FROST_PAPER_CHECKER)
                .save(output, "frost_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER_4, 2)
                .addInput(WenyanItems.LUNAR_INK.get(), 1)
                .addInput(WenyanItems.FROST_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_4_CHECKER)
                .save(output, "hand_runner_4");

        AnsweringRecipeBuilder
                .create(WenyanItems.ARCANE_INK, 4)
                .addInput(Items.ENCHANTED_BOOK, 1)
                .addInput(WenyanItems.LUNAR_INK.get(), 1)
                .question(CheckerFactory.ARCANE_INK_CHECKER)
                .save(output, "arcane_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.PHOENIX_PAPER, 4)
                .addInput(Items.BLAZE_POWDER, 1)
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerFactory.PHOENIX_PAPER_CHECKER)
                .save(output, "phoenix_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER_5, 2)
                .addInput(WenyanItems.ARCANE_INK.get(), 1)
                .addInput(WenyanItems.PHOENIX_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_5_CHECKER)
                .save(output, "hand_runner_5");
        AnsweringRecipeBuilder
                .create(WenyanItems.CELESTIAL_INK, 4)
                .addInput(Items.NETHERITE_SCRAP, 1)
                .addInput(WenyanItems.ARCANE_INK.get(), 1)
                .question(CheckerFactory.CELESTIAL_INK_CHECKER)
                .save(output, "celestial_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.DRAGON_PAPER, 4)
                .addInput(Items.DRAGON_BREATH, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerFactory.DRAGON_PAPER)
                .save(output, "dragon_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER_6, 2)
                .addInput(WenyanItems.CELESTIAL_INK.get(), 1)
                .addInput(WenyanItems.DRAGON_PAPER.get(), 1)
                .question(CheckerFactory.HAND_RUNNER_6_CHECKER)
                .save(output, "hand_runner_6");
    }

    private void clearDataRecipe(BlockItem item) {
        ShapelessRecipeBuilder
                .shapeless(BuiltInRegistries.ITEM, RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy("has_hand_runner", has(item))
                .save(output, item.getDescriptionId() + "_clear");
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
