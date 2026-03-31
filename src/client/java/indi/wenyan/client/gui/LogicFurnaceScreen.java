package indi.wenyan.client.gui;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.furnace.LogicFurnaceMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class LogicFurnaceScreen extends AbstractContainerScreen<LogicFurnaceMenu> {
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"textures/gui/logic_furnace_gui.png");
    private static final Identifier ARROW_TEXTURE =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"textures/gui/furnace_arrow.png");

    public LogicFurnaceScreen(LogicFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0,  imageWidth, imageHeight, 256, 256);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE,x + 81, y + 34, 0, 0, menu.getScaledArrowProgress(),  16, 24, 16);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.extractTooltip(graphics, mouseX, mouseY);
    }
}
