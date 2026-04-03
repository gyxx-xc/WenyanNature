package indi.wenyan.client.intergration.jei;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.recipe.answering.AnsweringRecipe;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.language.GuiText;
import lombok.Getter;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AnsweringCategory implements IRecipeCategory<RecipeHolder<AnsweringRecipe>> {
    public static final IRecipeType<RecipeHolder<AnsweringRecipe>> TYPE = IRecipeType.create(WyRegistration.ANSWERING_RECIPE_TYPE.get());
    private static final List<Coord> INGREDIENT_COORDS = List.of(
            new Coord(11, 83),
            new Coord(40, 108),
            new Coord(72, 108),
            new Coord(101, 83)
    );
    private static final Identifier BACKGROUND_TEXTURE =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/gui/answering_background.png");

    @Getter private final IDrawable icon;
    @Getter private final int width = 128;
    @Getter private final int height = 128;
    private final IDrawable background;
    private final IDrawable craftingBlock;

    public AnsweringCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemLike(WenyanItems.CRAFTING_BLOCK_ITEM);
        background = helper.drawableBuilder(BACKGROUND_TEXTURE, 0, 0, width, height).setTextureSize(width, height).build();
        craftingBlock = helper.createDrawableItemLike(WenyanItems.CRAFTING_BLOCK_ITEM);
    }

    @Override
    public IRecipeType<RecipeHolder<AnsweringRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return GuiText.JeiAnswerTitle.text();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AnsweringRecipe> recipe, IFocusGroup focuses) {
        List<Ingredient> inputted = recipe.value().input();
        for (int i = 0; i < inputted.size(); i++) {
            Coord coord = INGREDIENT_COORDS.get(i);
            builder.addInputSlot(coord.x, coord.y).add(inputted.get(i));
        }
        builder.addOutputSlot(55, 4).add(recipe.value().output().create());
    }

    @Override
    public void draw(RecipeHolder<AnsweringRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        background.draw(guiGraphics);
        craftingBlock.draw(guiGraphics, 56, 60);
    }

    private record Coord(int x, int y) { }
}
