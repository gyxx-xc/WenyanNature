package indi.wenyan.setup.datagen.recipe.craft;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.checker.CheckerEnum;
import indi.wenyan.content.recipe.combine_module.ThrowModuleRecipe;
import indi.wenyan.setup.datagen.recipe.AnsweringRecipeBuilder;
import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.datagen.recipe.RecipeUtilities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static indi.wenyan.setup.definitions.WenyanItems.THROW_RUNNER;
import static indi.wenyan.setup.definitions.WyRegistration.RUNNER_PAPER_ITEM;

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
        // === Hand Runner 0 & Misc ===
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0), 1,
                CheckerEnum.PLUS_CHECKER, 8,
                Items.PAPER);

        RecipeUtilities.newAnsweringRecipe(output, "diamond_labyrinth_checker",
                Items.DIAMOND, 1,
                CheckerEnum.LABYRINTH_CHECKER, 1,
                Items.COAL, Items.COAL, Items.COAL, Items.COAL);

        // Hand Runner 0
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0), 1,
                "III",
                "I I",
                "III",
                'I', Items.PAPER);

        // === Paper Group (Lv.1 - Lv.6) ===
        // 松竹纸 (Lv.1)
        RecipeUtilities.newAnsweringRecipe(output, "bamboo_paper",
                WenyanItems.BAMBOO_PAPER.get(), 4,
                CheckerEnum.PLUS_CHECKER, 1,
                Items.PAPER, Items.BAMBOO);

        // 云篆纸 (Lv.2)
        RecipeUtilities.newAnsweringRecipe(output, "cloud_paper",
                WenyanItems.CLOUD_PAPER.get(), 4,
                CheckerEnum.CLOUD_PAPER_CHECKER, 1,
                Items.FEATHER, Items.PAPER);

        // 星辉纸 (Lv.3)
        RecipeUtilities.newAnsweringRecipe(output, "star_paper",
                WenyanItems.STARLIGHT_PAPER.get(), 4,
                CheckerEnum.STARLIGHT_PAPER_CHECKER, 1,
                Items.PAPER, Items.GLOWSTONE_DUST);

        // 霜华纸 (Lv.4)
        RecipeUtilities.newAnsweringRecipe(output, "frost_paper",
                WenyanItems.FROST_PAPER.get(), 4,
                CheckerEnum.FROST_PAPER_CHECKER, 1,
                Items.SNOWBALL, Items.GOLD_NUGGET, Items.PAPER);

        // 凤羽纸 (Lv.5)
        RecipeUtilities.newAnsweringRecipe(output, "phoenix_paper",
                WenyanItems.PHOENIX_PAPER.get(), 4,
                CheckerEnum.PHOENIX_PAPER_CHECKER, 1,
                Items.BLAZE_POWDER, Items.FEATHER, Items.PAPER);

        // 龙鳞纸 (Lv.6)
        RecipeUtilities.newAnsweringRecipe(output, "dragon_paper",
                WenyanItems.DRAGON_PAPER.get(), 4,
                CheckerEnum.DRAGON_PAPER_CHECKER, 1,
                Items.DRAGON_BREATH, Items.PAPER);

        // === Ink Group (Lv.1 - Lv.6) ===
        // 松清墨 (Lv.1)
        RecipeUtilities.newAnsweringRecipe(output, "bamboo_ink",
                WenyanItems.BAMBOO_INK.get(), 4,
                CheckerEnum.PRINT_CHECKER, 1,
                Items.POTION, Items.BLACK_DYE);

        // 朱砂墨 (Lv.2)
        RecipeUtilities.newAnsweringRecipe(output, "cinnabar_ink",
                WenyanItems.CINNABAR_INK.get(), 4,
                CheckerEnum.CINNABAR_INK_CHECKER, 1,
                Items.POTION, Items.BLACK_DYE, Items.REDSTONE);

        // 星光墨 (Lv.3)
        RecipeUtilities.newAnsweringRecipe(output, "starlight_ink",
                WenyanItems.STARLIGHT_INK.get(), 4,
                CheckerEnum.STARLIGHT_INK_CHECKER, 1,
                Items.POTION, Items.BLACK_DYE, Items.GLOWSTONE_DUST);

        // 月华墨 (Lv.4)
        RecipeUtilities.newAnsweringRecipe(output, "lunar_ink",
                WenyanItems.LUNAR_INK.get(), 4,
                CheckerEnum.LUNAR_INK_CHECKER, 1,
                WenyanItems.STARLIGHT_INK.get(), Items.BLACK_DYE);

        // 玄武墨 (Lv.5)
        RecipeUtilities.newAnsweringRecipe(output, "arcane_ink",
                WenyanItems.ARCANE_INK.get(), 4,
                CheckerEnum.ARCANE_INK_CHECKER, 1,
                Items.ENCHANTED_BOOK, WenyanItems.LUNAR_INK.get());

        // 冥土墨 (Lv.6)
        RecipeUtilities.newAnsweringRecipe(output, "celestial_ink",
                WenyanItems.CELESTIAL_INK.get(), 4,
                CheckerEnum.CELESTIAL_INK_CHECKER, 1,
                Items.NETHERITE_SCRAP, WenyanItems.ARCANE_INK.get());

        // === Hand Runner Tier Group (Tier 1 - Tier 6) ===
        // Hand Runner 1
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_1",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_1), 2,
                CheckerEnum.HAND_RUNNER_1_CHECKER, 1,
                WenyanItems.BAMBOO_INK.get(), WenyanItems.BAMBOO_PAPER.get());

        // Hand Runner 2
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_2",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_2), 2,
                CheckerEnum.HAND_RUNNER_2_CHECKER, 1,
                WenyanItems.CINNABAR_INK.get(), WenyanItems.CLOUD_PAPER.get());

        // Hand Runner 3
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_3",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_3), 2,
                CheckerEnum.HAND_RUNNER_3_CHECKER, 1,
                WenyanItems.STARLIGHT_INK.get(), WenyanItems.STARLIGHT_PAPER.get());

        // Hand Runner 4
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_4",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_4), 2,
                CheckerEnum.HAND_RUNNER_4_CHECKER, 1,
                WenyanItems.LUNAR_INK.get(), WenyanItems.FROST_PAPER.get());

        // Hand Runner 5
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_5",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_5), 2,
                CheckerEnum.HAND_RUNNER_5_CHECKER, 1,
                WenyanItems.ARCANE_INK.get(), WenyanItems.PHOENIX_PAPER.get());

        // Hand Runner 6
        RecipeUtilities.newAnsweringRecipe(output, "hand_runner_6",
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_6), 2,
                CheckerEnum.HAND_RUNNER_6_CHECKER, 1,
                WenyanItems.CELESTIAL_INK.get(), WenyanItems.DRAGON_PAPER.get());

        // == Throwable Runner ===
        RecipeUtilities.newShapedRecipe(provider, output, items, RecipeCategory.MISC,
                WenyanItems.THROW_MODULE, 1,
                " p ",
                " i ",
                " p ",
                'p', RUNNER_PAPER_ITEM, "has_paper",
                'i', Items.GUNPOWDER);

        for (RunnerTier tier : RunnerTier.values()) {
            RecipeUtilities.newShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                    "throw_" + tier.name().toLowerCase(),
                    THROW_RUNNER.getItem(tier), 1,
                    WenyanItems.HAND_RUNNER.getItem(tier), WenyanItems.THROW_MODULE);
        }
    }

    public static void clearDataRecipe(HolderGetter<Item> items, RecipeOutput output, Item item, CheckerRecipeProvider provider) {
        RecipeUtilities.newShapelessRecipe(provider, items, output, RecipeCategory.MISC,
                item.getDescriptionId().substring(item.getDescriptionId().lastIndexOf(".") + 1) + "_clean",
                item, 1,
                item);
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
