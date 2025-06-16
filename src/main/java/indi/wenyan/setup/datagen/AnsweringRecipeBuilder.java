package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanNature;
import indi.wenyan.content.recipe.AnsweringRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AnsweringRecipeBuilder {
    private final List<Ingredient> input = new ArrayList<>();
    private final ItemStack output;
    private String question;
    private Criterion<?> criterion;

    private AnsweringRecipeBuilder(ItemStack output){
        this.output = output;
    }

    public static AnsweringRecipeBuilder create(Item item) {
        return new AnsweringRecipeBuilder(item.getDefaultInstance());
    }

    public static AnsweringRecipeBuilder create(ItemStack output) {
        return new AnsweringRecipeBuilder(output);
    }

    public AnsweringRecipeBuilder addInput(Ingredient ingredient) {
        this.input.add(ingredient);
        return this;
    }

    public AnsweringRecipeBuilder addInput(Item item) {
        return addInput(Ingredient.of(item));
    }

    public AnsweringRecipeBuilder addInput(ItemStack itemStack) {
        return addInput(Ingredient.of(itemStack));
    }

    public AnsweringRecipeBuilder addInput(List<Item> items) {
        for (Item item : items) {
            addInput(item);
        }
        return this;
    }

    public AnsweringRecipeBuilder unlock(Criterion<?> criterion) {
        this.criterion = criterion;
        return this;
    }

    public AnsweringRecipeBuilder question(String question) {
        this.question = question;
        return this;
    }

    public void save(RecipeOutput recipeOutput, String recipeName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, recipeName);
        recipeOutput.accept(
                id,
                new AnsweringRecipe(input, question, output),
                recipeOutput.advancement()
                        .addCriterion("has_" + recipeName, criterion)
                        .build(id.withPrefix("recipes/"))
        );
    }
}
