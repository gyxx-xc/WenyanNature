package indi.wenyan.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record AnsweringRecipe(List<Ingredient> input, String question, ItemStack output) implements Recipe<AnsweringRecipeInput> {

    @Override
    public boolean matches(AnsweringRecipeInput answeringRecipeInput, Level level) {
        if (level.isClientSide() || input.size() != answeringRecipeInput.size()){
            return false;
        }

        var item = answeringRecipeInput.input().stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();

        StackedContents stackedContents = new StackedContents();
        for (ItemStack stack : item)
            stackedContents.accountStack(stack, 1);

        return RecipeMatcher.findMatches(item, input) != null;
    }

    @Override
    public ItemStack assemble(AnsweringRecipeInput answeringRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.ANSWERING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.ANSWERING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AnsweringRecipe> {
        public static final MapCodec<AnsweringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.listOf().fieldOf("input").forGetter(AnsweringRecipe::input),
                Codec.STRING.fieldOf("question").forGetter(AnsweringRecipe::question),
                ItemStack.CODEC.fieldOf("output").forGetter(AnsweringRecipe::output)
        ).apply(inst, AnsweringRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AnsweringRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Serializers.INGREDIENT_LIST_STREAM, AnsweringRecipe::input,
                        ByteBufCodecs.STRING_UTF8, AnsweringRecipe::question,
                        ItemStack.STREAM_CODEC, AnsweringRecipe::output,
                        AnsweringRecipe::new);

        @Override
        public MapCodec<AnsweringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnsweringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
