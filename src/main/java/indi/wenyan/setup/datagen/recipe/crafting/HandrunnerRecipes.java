package indi.wenyan.setup.datagen.recipe.crafting;

import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.datagen.recipe.RecipeUtilities;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class HandrunnerRecipes {
    public static void build(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        buildLevel0(items, output, provider);
        buildLevel1(items, output, provider);
        buildLevel2(items, output, provider);
        buildLevel3(items, output, provider);
        buildLevel4(items, output, provider);
        buildLevel5(items, output, provider);
        buildLevel6(items, output, provider);
    }

    private static void buildLevel0(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapedRecipe(provider, output, items, RecipeCategory.MISC,
                "hand_runner_0_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0), 1,
                "ppp",
                "pip",
                "ppp",
                'p', Items.PAPER,
                'i', Items.IRON_INGOT);
    }

    private static void buildLevel1(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "bamboo_ink_crafting",
                WenyanItems.BAMBOO_INK.get(), 1,
                Items.POTION, Items.BLACK_DYE, Items.BAMBOO, Items.COPPER_INGOT);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "bamboo_paper_crafting",
                WenyanItems.BAMBOO_PAPER.get(), 1,
                Items.PAPER, Items.BAMBOO, Items.STRING);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_1_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_1), 1,
                WenyanItems.BAMBOO_INK.get(), WenyanItems.BAMBOO_PAPER.get());
    }

    private static void buildLevel2(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "cinnabar_ink_crafting",
                WenyanItems.CINNABAR_INK.get(), 1,
                Items.POTION, Items.REDSTONE, Items.BLACK_DYE, Items.GOLD_INGOT);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "cloud_paper_crafting",
                WenyanItems.CLOUD_PAPER.get(), 1,
                Items.PAPER, Items.FEATHER, Items.PHANTOM_MEMBRANE);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_2_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_2), 1,
                WenyanItems.CINNABAR_INK.get(), WenyanItems.CLOUD_PAPER.get());
    }

    private static void buildLevel3(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "starlight_ink_crafting",
                WenyanItems.STARLIGHT_INK.get(), 1,
                Items.POTION, Items.GLOWSTONE_DUST, Items.AMETHYST_SHARD, Items.GOLD_INGOT);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "star_paper_crafting",
                WenyanItems.STARLIGHT_PAPER.get(), 1,
                Items.PAPER, Items.GLOWSTONE_DUST, Items.FEATHER, Items.AMETHYST_SHARD);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_3_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_3), 1,
                WenyanItems.STARLIGHT_INK.get(), WenyanItems.STARLIGHT_PAPER.get());
    }

    private static void buildLevel4(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "lunar_ink_crafting",
                WenyanItems.LUNAR_INK.get(), 1,
                Items.POTION, WenyanItems.STARLIGHT_INK.get(), Items.QUARTZ, Items.DIAMOND);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "frost_paper_crafting",
                WenyanItems.FROST_PAPER.get(), 1,
                Items.PAPER, Items.SNOWBALL, Items.GOLD_NUGGET, Items.PACKED_ICE);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_4_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_4), 1,
                WenyanItems.LUNAR_INK.get(), WenyanItems.FROST_PAPER.get());
    }

    private static void buildLevel5(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "arcane_ink_crafting",
                WenyanItems.ARCANE_INK.get(), 1,
                Items.POTION, WenyanItems.LUNAR_INK.get(), Items.ENCHANTED_BOOK, Items.DIAMOND);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "phoenix_paper_crafting",
                WenyanItems.PHOENIX_PAPER.get(), 1,
                Items.PAPER, Items.BLAZE_POWDER, Items.FEATHER, Items.DIAMOND);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_5_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_5), 1,
                WenyanItems.ARCANE_INK.get(), WenyanItems.PHOENIX_PAPER.get());
    }

    private static void buildLevel6(HolderGetter<Item> items, RecipeOutput output, CheckerRecipeProvider provider) {
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "celestial_ink_crafting",
                WenyanItems.CELESTIAL_INK.get(), 1,
                Items.POTION, WenyanItems.ARCANE_INK.get(), Items.NETHERITE_SCRAP, Items.END_CRYSTAL);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "dragon_paper_crafting",
                WenyanItems.DRAGON_PAPER.get(), 1,
                Items.PAPER, Items.PHANTOM_MEMBRANE, Items.DIAMOND, Items.DRAGON_BREATH);
        RecipeUtilities.newModShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                "hand_runner_6_crafting",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_6), 1,
                WenyanItems.CELESTIAL_INK.get(), WenyanItems.DRAGON_PAPER.get());
    }
}
