package indi.wenyan.content.gui;

import indi.wenyan.WenyanProgramming;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@Deprecated
@OnlyIn(Dist.CLIENT)
public class CraftingBlockScreen extends AbstractContainerScreen<CraftingBlockContainer> {

    private static final Identifier GUI = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
            "textures/gui/crafting_gui.png");

    public CraftingBlockScreen(CraftingBlockContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
//        int relX = (width - imageWidth) / 2;
//        int relY = (height - imageHeight) / 2;
//        graphics.blit(GUI, relX, relY, 0, 0, imageWidth, imageHeight);
//        graphics.blit(GUI, relX+59, relY+108, 1, 202+menu.getResult()*11, menu.getProgress(58), 9);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
