package indi.wenyan.content.recipe;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import indi.wenyan.setup.definitions.WyRegistration;
import lombok.AccessLevel;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Accessors(fluent = true)
@Value
public class AnsweringRecipe implements Recipe<AnsweringRecipeInput> {
    List<Ingredient> input;
    String question;
    ItemStack output;
    int round;

    @With(AccessLevel.PRIVATE)
    @NonFinal
    PlacementInfo info;

    public AnsweringRecipe(List<Ingredient> input, String question, ItemStack output, int round, PlacementInfo info) {
        this.input = input;
        this.question = question;
        this.output = output;
        this.round = round;
        this.info = info;
    }

    public AnsweringRecipe(List<Ingredient> input, String question, ItemStack output, int round) {
        this.input = input;
        this.question = question;
        this.output = output;
        this.round = round;
    }


    public static final String ID = "answering_recipe";
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_STREAM =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new));

    @Override
    public boolean matches(AnsweringRecipeInput answeringRecipeInput, Level level) {
        if (level.isClientSide() || input.size() != answeringRecipeInput.size()) {
            return false;
        }

        var item = answeringRecipeInput.input().stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();

        StackedContents stackedContents = new StackedContents();
        for (ItemStack stack : item)
            stackedContents.account(stack, 1);

        return RecipeMatcher.findMatches(item, input) != null;
    }

    @Override
    public ItemStack assemble(AnsweringRecipeInput answeringRecipeInput) {
        return output.copy();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return WyRegistration.CALCULATION_BLOCK_CATEGORY.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // recomand from https://docs.neoforged.net/docs/resources/server/recipes/custom
        if (info == null) {
            this.info = PlacementInfo.create(input);
        }
        return info;
    }

    @Override
    public RecipeSerializer<? extends Recipe<AnsweringRecipeInput>> getSerializer() {
        return WyRegistration.ANSWERING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<AnsweringRecipeInput>> getType() {
        return WyRegistration.ANSWERING_RECIPE_TYPE.get();
    }

    public static class SerializerProvider {
        public static final MapCodec<AnsweringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.listOf().fieldOf("input").forGetter(AnsweringRecipe::input),
                Codec.STRING.fieldOf("question").forGetter(AnsweringRecipe::question),
                ItemStack.CODEC.fieldOf("output").forGetter(AnsweringRecipe::output),
                Codec.INT.fieldOf("round").forGetter(AnsweringRecipe::round)
        ).apply(inst, AnsweringRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AnsweringRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        INGREDIENT_LIST_STREAM, AnsweringRecipe::input,
                        ByteBufCodecs.STRING_UTF8, AnsweringRecipe::question,
                        ItemStack.STREAM_CODEC, AnsweringRecipe::output,
                        ByteBufCodecs.INT, AnsweringRecipe::round,
                        AnsweringRecipe::new);

        public static RecipeSerializer<AnsweringRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}
