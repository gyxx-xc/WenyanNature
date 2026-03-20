package indi.wenyan.content.recipe.combine_module;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import indi.wenyan.content.item.throw_runner.FuContainerComponent;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrowModuleRecipe extends CustomRecipe {
    public static final MapCodec<ThrowModuleRecipe> MAP_CODEC =
            RecordCodecBuilder.mapCodec((i) -> i
                    .group(Ingredient.CODEC.fieldOf("target").forGetter((o) -> o.target))
                    .apply(i, ThrowModuleRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ThrowModuleRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    (o) -> o.target,
                    ThrowModuleRecipe::new
            );
    public static final RecipeSerializer<ThrowModuleRecipe> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public static final String ID = "throw_module_recipe";

    private final Ingredient target;

    public ThrowModuleRecipe(Ingredient target) {
        this.target = target;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasTarget = false;

        for (int slot = 0; slot < input.size(); ++slot) {
            ItemStack itemStack = input.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (this.target.test(itemStack)) {
                    if (hasTarget)
                        return false;

                    hasTarget = true;
                } else {
                    if (!itemStack.is(WyRegistration.MODULE_ITEM))
                        return false;
                }
            }
        }

        return hasTarget;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput) {
        ItemStack result = ItemStack.EMPTY;
        List<ItemStack> modules = new ArrayList<>();
        for (int slot = 0; slot < craftingInput.size(); slot++) {
            ItemStack stack = craftingInput.getItem(slot);
            if (!stack.isEmpty()) {
                if (this.target.test(stack)) {
                    result = stack.copy();
                } else {
                    modules.add(stack);
                }
            }
        }
        result.set(WyRegistration.FU_DATA, result.getOrDefault(WyRegistration.FU_DATA, FuContainerComponent.EMPTY).withNewItemStacks(modules));
        result.setCount(1);
        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return WyRegistration.THROW_MODULE_RECIPE_SERIALIZER.get();
    }
}
