package indi.wenyan.client;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.recipe.answering.AnsweringRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.ArrayList;
import java.util.List;

@Mod(value = WenyanProgramming.MODID, dist = Dist.CLIENT)
public class WenyanProgrammingClient {
    public static final List<RecipeHolder<AnsweringRecipe>> ALL_ANSWERING_RECIPES = new ArrayList<>();

    public WenyanProgrammingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (_, parent) -> new ConfigurationScreen(container, parent));
    }
}
