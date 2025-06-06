package indi.wenyan.content.gui;

import indi.wenyan.WenyanNature;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingBlockScreen extends AbstractContainerScreen<CraftingBlockContainer> {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "textures/gui/crafting_gui.png");

    public CraftingBlockScreen(CraftingBlockContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
        // make center
        this.titleLabelX = 63;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.index == 36) {
            var list = new ArrayList<Component>();
            list.add(Component.translatable("title.wenyan_nature.create_tab").withStyle(ChatFormatting.GREEN));
            guiGraphics.renderTooltip(this.font, list,
                    java.util.Optional.empty(), ItemStack.EMPTY, x, y);
        }
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem() && this.hoveredSlot.index != 36) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, x, y);
        }
    }

//    @Override
//    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
//    }
}
