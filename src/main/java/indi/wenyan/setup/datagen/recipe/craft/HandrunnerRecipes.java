package indi.wenyan.setup.datagen.recipe.craft;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.checker.CheckerEnum;
import indi.wenyan.content.recipe.combine_module.ThrowModuleRecipe;
import indi.wenyan.setup.datagen.recipe.AnsweringRecipeBuilder;
import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static indi.wenyan.setup.definitions.WenyanItems.THROW_RUNNER;

public class HandrunnerRecipes {
    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        for (var item : WenyanItems.HAND_RUNNER.getItems()) {
            clearDataRecipe(items, output, item, provider);
        }
        for (var item : THROW_RUNNER.getItems()) {
            clearDataRecipe(items, output, item, provider);
        }

        for (var item : THROW_RUNNER.getItems()) {
            throwAddModuleRecipe(output, item);
        }
        // === Hand Runner ===
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0))
                .addInput(Items.PAPER)
                .question(CheckerEnum.PLUS_CHECKER)
                .round(8)
                .save(output, "hand_runner");
        AnsweringRecipeBuilder
                .create(Items.DIAMOND)
                .addInput(Items.COAL, 4)
                .question(CheckerEnum.LABYRINTH_CHECKER)
                .save(output, "diamond_labyrinth_checker");

        ShapedRecipeBuilder // Hand Runner 0
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0))
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.PAPER)
                .unlockedBy("has_paper", provider.publicHas(Items.PAPER))
                .save(output);

        // Hand Runner 1
        AnsweringRecipeBuilder // bamboo ink
                .create(WenyanItems.BAMBOO_INK.get(), 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .question(CheckerEnum.PRINT_CHECKER)
                .save(output, "bamboo_ink");
        AnsweringRecipeBuilder // bamboo paper
                .create(WenyanItems.BAMBOO_PAPER.get(), 4)
                .addInput(Items.PAPER, 1)
                .addInput(Items.BAMBOO, 1)
                .question(CheckerEnum.PLUS_CHECKER)
                .save(output, "bamboo_paper");
        AnsweringRecipeBuilder // Hand Runner 1
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_1), 2)
                .addInput(WenyanItems.BAMBOO_INK.get(), 1)
                .addInput(WenyanItems.BAMBOO_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_1_CHECKER)
                .save(output, "hand_runner_1");

        // Hand Runner 2
        AnsweringRecipeBuilder // Cinnabar Ink
                .create(WenyanItems.CINNABAR_INK.get(), 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .addInput(Items.REDSTONE, 1)
                .question(CheckerEnum.CINNABAR_INK_CHECKER)
                .save(output, "cinnabar_ink");
        AnsweringRecipeBuilder // Cloud Paper
                .create(WenyanItems.CLOUD_PAPER.get(), 4)
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerEnum.CLOUD_PAPER_CHECKER)
                .save(output, "cloud_paper");
        AnsweringRecipeBuilder // Hand Runner 2
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_2), 2)
                .addInput(WenyanItems.CINNABAR_INK.get(), 1)
                .addInput(WenyanItems.CLOUD_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_2_CHECKER)
                .save(output, "hand_runner_2");

        // Hand Runner 3
        AnsweringRecipeBuilder
                .create(WenyanItems.STARLIGHT_INK.get(), 4)
                .addInput(Items.POTION, 1)
                .addInput(Items.BLACK_DYE, 1)
                .addInput(Items.GLOWSTONE_DUST, 1)
                .question(CheckerEnum.STARLIGHT_INK_CHECKER)
                .save(output, "starlight_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.STARLIGHT_PAPER.get(), 4)
                .addInput(Items.PAPER, 1)
                .addInput(Items.GLOWSTONE_DUST, 1)
                .question(CheckerEnum.STARLIGHT_PAPER_CHECKER) // Need change
                .save(output, "star_paper");
        AnsweringRecipeBuilder // Hand Runner 3
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_3), 2)
                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                .addInput(WenyanItems.STARLIGHT_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_3_CHECKER) // Need change
                .save(output, "hand_runner_3");

        // Hand Runner 4
        AnsweringRecipeBuilder // Lunar ink
                .create(WenyanItems.LUNAR_INK.get(), 4)
                .addInput(WenyanItems.STARLIGHT_INK.get(), 1)
                .addInput(Items.BLACK_DYE, 1)
                .question(CheckerEnum.LUNAR_INK_CHECKER)
                .save(output, "lunar_ink");
        AnsweringRecipeBuilder // frost_paper
                .create(WenyanItems.FROST_PAPER.get(), 4)
                .addInput(Items.SNOWBALL, 1)
                .addInput(Items.GOLD_NUGGET, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerEnum.FROST_PAPER_CHECKER)
                .save(output, "frost_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_4), 2)
                .addInput(WenyanItems.LUNAR_INK.get(), 1)
                .addInput(WenyanItems.FROST_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_4_CHECKER)
                .save(output, "hand_runner_4");

        // Hand Runner 5
        AnsweringRecipeBuilder
                .create(WenyanItems.ARCANE_INK.get(), 4)
                .addInput(Items.ENCHANTED_BOOK, 1)
                .addInput(WenyanItems.LUNAR_INK.get(), 1)
                .question(CheckerEnum.ARCANE_INK_CHECKER)
                .save(output, "arcane_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.PHOENIX_PAPER.get(), 4)
                .addInput(Items.BLAZE_POWDER, 1)
                .addInput(Items.FEATHER, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerEnum.PHOENIX_PAPER_CHECKER)
                .save(output, "phoenix_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_5), 2)
                .addInput(WenyanItems.ARCANE_INK.get(), 1)
                .addInput(WenyanItems.PHOENIX_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_5_CHECKER)
                .save(output, "hand_runner_5");

        // Hand Runner 6
        AnsweringRecipeBuilder
                .create(WenyanItems.CELESTIAL_INK.get(), 4)
                .addInput(Items.NETHERITE_SCRAP, 1)
                .addInput(WenyanItems.ARCANE_INK.get(), 1)
                .question(CheckerEnum.CELESTIAL_INK_CHECKER)
                .save(output, "celestial_ink");
        AnsweringRecipeBuilder
                .create(WenyanItems.DRAGON_PAPER.get(), 4)
                .addInput(Items.DRAGON_BREATH, 1)
                .addInput(Items.PAPER, 1)
                .question(CheckerEnum.DRAGON_PAPER_CHECKER)
                .save(output, "dragon_paper");
        AnsweringRecipeBuilder
                .create(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_6), 2)
                .addInput(WenyanItems.CELESTIAL_INK.get(), 1)
                .addInput(WenyanItems.DRAGON_PAPER.get(), 1)
                .question(CheckerEnum.HAND_RUNNER_6_CHECKER)
                .save(output, "hand_runner_6");

        // == Throwable Runner ===
        Ingredient paperIngredient = Ingredient.of(
                WenyanItems.BAMBOO_PAPER.get(),
                WenyanItems.CLOUD_PAPER.get(),
                WenyanItems.FROST_PAPER.get(),
                WenyanItems.PHOENIX_PAPER.get(),
                WenyanItems.STARLIGHT_PAPER.get(),
                WenyanItems.DRAGON_PAPER.get());

        ShapedRecipeBuilder
                .shaped(items, RecipeCategory.MISC,
                        WenyanItems.THROW_MODULE)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', paperIngredient)
                .define('i', Items.GUNPOWDER)
                .unlockedBy("has_paper", provider.publicHas(WenyanItems.BAMBOO_PAPER.get()))
                .unlockedBy("has_gunpowder", provider.publicHas(Items.GUNPOWDER))
                .save(output);

        for (RunnerTier tier : RunnerTier.values()) {
            ShapelessRecipeBuilder
                    .shapeless(items, RecipeCategory.MISC,
                            THROW_RUNNER.getItem(tier))
                    .requires(WenyanItems.HAND_RUNNER.getItem(tier))
                    .requires(WenyanItems.THROW_MODULE)
                    .unlockedBy("has_hand_runner_" + tier.name().toLowerCase(),
                            provider.publicHas(WenyanItems.HAND_RUNNER.getItem(tier)))
                    .save(output, "throw_" + tier.name().toLowerCase());
        }
    }

    public static void clearDataRecipe(HolderGetter<Item> items, RecipeOutput output, Item item, CheckerRecipeProvider provider) {
        ShapelessRecipeBuilder
                .shapeless(items, RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy("has_hand_runner", provider.publicHas(item))
                .save(output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                // FIXME
                                item.getDescriptionId()
                                        .substring(item.getDescriptionId()
                                                .lastIndexOf(".") + 1)
                                        + "_clean")));
    }

    public static void throwAddModuleRecipe(RecipeOutput output, Item item) {
        SpecialRecipeBuilder.special(() -> new ThrowModuleRecipe(Ingredient.of(item)))
                .save(output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                // FIXME
                                item.getDescriptionId()
                                        .substring(item.getDescriptionId()
                                                .lastIndexOf(".") + 1)
                                        + "_module")));
    }

}
