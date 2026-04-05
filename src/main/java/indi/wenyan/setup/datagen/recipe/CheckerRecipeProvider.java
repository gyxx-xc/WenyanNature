package indi.wenyan.setup.datagen.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.checker.CheckerEnum;
import indi.wenyan.content.item.throw_runner.ThrowRunnerItem;
import indi.wenyan.content.recipe.combine_module.ThrowModuleRecipe;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

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

    @Override
    protected void buildRecipes() {
        // recipe that clear code in runner
        for (var item : WenyanItems.HAND_RUNNER.getItems()) {
            clearDataRecipe(item);
        }
        for (var item : THROW_RUNNER.getItems()) {
            clearDataRecipe(item);
        }

        for (var item : THROW_RUNNER.getItems()) {
            throwAddModuleRecipe(item);
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
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0))
                .pattern("III")
                .pattern("I I")
                .pattern("III")
                .define('I', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
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
                WenyanItems.DRAGON_PAPER.get()
        );

        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.THROW_MODULE)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', paperIngredient)
                .define('i', Items.GUNPOWDER)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        for (RunnerTier tier : RunnerTier.values()) {
            ShapelessRecipeBuilder
                    .shapeless(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                            THROW_RUNNER.getItem(tier))
                    .requires(WenyanItems.HAND_RUNNER.getItem(tier))
                    .requires(WenyanItems.THROW_MODULE)
                    .unlockedBy("has_hand_runner_" + tier.name().toLowerCase(),
                            has(WenyanItems.HAND_RUNNER.getItem(tier)))
                    .save(output, "throw_" + tier.name().toLowerCase());
        }
        // === Tool and Module ===
        // 印符 (print_inventory_module) - 无序
        ShapelessRecipeBuilder
                .shapeless(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.PRINT_INVENTORY_MODULE, 2)
                .requires(Items.PAPER)
                .requires(Ingredient.of(Items.COAL, Items.CHARCOAL))
                .requires(Items.FEATHER)
                .unlockedBy("has_paper", has(Items.PAPER)).save(output);

        // 位元符 (bit_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.BIT_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.REDSTONE)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 数符 (math_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.MATH_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.COPPER_INGOT)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 向量符 (vec3_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.VEC3_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.IRON_INGOT)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 熵符 (random_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.RANDOM_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.ROTTEN_FLESH)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 字串符 (string_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.STRING_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.STRING)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 集符 (collection_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.COLLECTION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.BAMBOO_PAPER.get())
                .define('i', Items.BOWL)
                .unlockedBy("has_paper", has(WenyanItems.BAMBOO_PAPER.get())).save(output);

        // 物品符 (item_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.ITEM_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.CLOUD_PAPER.get())
                .define('i', Items.CHEST)
                .unlockedBy("has_paper", has(WenyanItems.CLOUD_PAPER.get())).save(output);

        // 方块符 (block_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.BLOCK_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.CLOUD_PAPER.get())
                .define('i', Items.SMOOTH_STONE)
                .unlockedBy("has_paper", has(WenyanItems.CLOUD_PAPER.get())).save(output);

        // 实体符 (entity_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.ENTITY_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.STARLIGHT_PAPER.get())
                .define('i', Items.EGG)
                .unlockedBy("has_paper", has(WenyanItems.STARLIGHT_PAPER.get())).save(output);

        // 天下情报符 (information_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.INFORMATION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.COMPASS)
                .unlockedBy("has_paper", has(WenyanItems.FROST_PAPER.get())).save(output);

        // 爆裂符 (explosion_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.FIRE_CHARGE)
                .unlockedBy("has_paper", has(WenyanItems.FROST_PAPER.get())).save(output);

        // 阻塞队列符 (blocking_queue_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.STARLIGHT_PAPER.get())
                .define('i', Items.HOPPER)
                .unlockedBy("has_paper", has(WenyanItems.STARLIGHT_PAPER.get())).save(output);

        // 活塞符 (piston_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.PISTON_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.FROST_PAPER.get())
                .define('i', Items.STICKY_PISTON)
                .unlockedBy("has_paper", has(WenyanItems.FROST_PAPER.get())).save(output);

        // 通讯符 (communicate_module_block)
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC,
                        WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM, 2)
                .pattern(" p ")
                .pattern(" i ")
                .pattern(" p ")
                .define('p', WenyanItems.STARLIGHT_PAPER.get())
                .define('i', Items.FIREWORK_ROCKET)
                .unlockedBy("has_paper", has(WenyanItems.STARLIGHT_PAPER.get())).save(output);

        // === Items ===
        // float_note
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, WenyanItems.FLOAT_NOTE.get())
                .pattern("PG")
                .define('P', Items.PAPER)
                .define('G', Items.GLOWSTONE_DUST)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);

        // === Blocks ===
        Ingredient cobblestoneIngredient = Ingredient.of(
                Items.COBBLESTONE,
                Items.ANDESITE,
                Items.DIORITE,
                Items.GRANITE,
                Items.BLACKSTONE,
                Items.COBBLED_DEEPSLATE
        );

        // crafting_block_item
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, WenyanItems.CRAFTING_BLOCK_ITEM.get())
                .pattern(" C ")
                .pattern(" T ")
                .pattern(" B ")
                .define('C', cobblestoneIngredient)
                .define('T', Items.CRAFTING_TABLE)
                .define('B', Items.STONE_BRICKS)
                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                .save(output);

        // pedestal_block
        ShapedRecipeBuilder
                .shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, WenyanItems.PEDESTAL_BLOCK_ITEM.get())
                .pattern(" C ")
                .pattern(" I ")
                .define('C', cobblestoneIngredient)
                .define('I', Items.IRON_BLOCK)
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .save(output);
    }

    private void clearDataRecipe(Item item) {
        ShapelessRecipeBuilder
                .shapeless(BuiltInRegistries.ITEM, RecipeCategory.MISC, item)
                .requires(item)
                .unlockedBy("has_hand_runner", has(item))
                .save(output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                // FIXME
                                item.getDescriptionId()
                                        .substring(item.getDescriptionId()
                                                .lastIndexOf(".") + 1)
                                        + "_clean")));
    }

    private void throwAddModuleRecipe(Item item) {
        SpecialRecipeBuilder.special(() -> new ThrowModuleRecipe(Ingredient.of(item)))
                .save(output, ResourceKey.create(Registries.RECIPE,
                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                // FIXME
                                item.getDescriptionId()
                                        .substring(item.getDescriptionId()
                                                .lastIndexOf(".") + 1)
                                        + "_module")));
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
