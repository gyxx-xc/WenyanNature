package indi.wenyan.content.gui;

import indi.wenyan.WenyanNature;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CraftingBlockScreen extends AbstractContainerScreen<CraftingBlockContainer> {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "textures/gui/crafting_gui.png");

    public CraftingBlockScreen(CraftingBlockContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, imageWidth, this.imageHeight);
        graphics.blit(GUI, relX+59, relY+108, 1, 202+menu.getResult()*11, menu.getProgress(58), 9);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
