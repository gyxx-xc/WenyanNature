package indi.wenyan.setup.datagen.recipe;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.recipe.AnsweringRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating answering recipes.
 * Follows the builder pattern for convenient recipe creation.
 */
@SuppressWarnings("unused")
public final class AnsweringRecipeBuilder {
    private final List<Ingredient> input = new ArrayList<>();
    private final ItemStack output;
    private String question;
    private Criterion<?> criterion;
    private int round = 1;

    /**
     * Private constructor to enforce use of factory methods.
     * @param output The output ItemStack produced by the recipe
     */
    private AnsweringRecipeBuilder(ItemStack output){
        this.output = output;
    }

    /**
     * Creates a new builder for a recipe with the specified item as output.
     * @param item The output item
     * @return A new builder instance
     */
    public static AnsweringRecipeBuilder create(Item item) {
        return new AnsweringRecipeBuilder(item.getDefaultInstance());
    }

    /**
     * Creates a new builder for a recipe with the specified itemstack as output.
     * @param output The output itemstack
     * @return A new builder instance
     */
    public static AnsweringRecipeBuilder create(ItemStack output) {
        return new AnsweringRecipeBuilder(output);
    }

    /**
     * Adds an ingredient to the recipe inputs.
     * @param ingredient The ingredient to add
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder addInput(Ingredient ingredient) {
        input.add(ingredient);
        return this;
    }

    /**
     * Adds multiple instances of an item to the recipe inputs.
     * @param item The item to add
     * @param count The number of instances to add
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder addInput(Item item, int count) {
        for (int i = 0; i < count; i++) {
            addInput(item);
        }
        return this;
    }

    /**
     * Adds an item to the recipe inputs.
     * @param item The item to add
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder addInput(Item item) {
        return addInput(Ingredient.of(item));
    }

    /**
     * Adds multiple items to the recipe inputs.
     * @param items The items to add
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder addInput(Iterable<Item> items) {
        for (Item item : items) {
            addInput(item);
        }
        return this;
    }

    /**
     * Sets the unlock criterion for the recipe advancement.
     * @param criterion The criterion to use
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder unlock(Criterion<?> criterion) {
        this.criterion = criterion;
        return this;
    }

    /**
     * Sets the question for the answering recipe.
     * @param question The question string
     * @return This builder for chaining
     */
    public AnsweringRecipeBuilder question(String question) {
        this.question = question;
        return this;
    }

    public AnsweringRecipeBuilder round(int round) {
        this.round = round;
        return this;
    }

    /**
     * Saves the recipe to the recipe output with the given name.
     * @param recipeOutput The recipe output to save to
     * @param recipeName The name for the recipe
     */
    public void save(RecipeOutput recipeOutput, String recipeName) {
        Identifier id = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, recipeName);
        recipeOutput.accept(
                id,
                new AnsweringRecipe(input, question, output, round),
                recipeOutput.advancement()
                        .addCriterion("has_" + recipeName, criterion)
                        .build(id.withPrefix("recipes/"))
        );
    }
}
