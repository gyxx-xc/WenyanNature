package indi.wenyan.content.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AnsweringRecipeInput(List<ItemStack> input) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int i) {
        return input.get(i);
    }

    @Override
    public int size() {
        return input.size();
    }
}
