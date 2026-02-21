package indi.wenyan.content.gui.code_editor.widget;

import indi.wenyan.WenyanProgramming;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;


// copy from FittingMultiLineTextWidget
@OnlyIn(Dist.CLIENT)
public class CodeOutputWidget extends AbstractScrollWidget {
    private final MultiLineTextWidget multilineWidget;
//    public static Identifier BACKGROUND = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"guantai.png");
    private int imageWidth = 256;
    private int imageHeight = 142;

    public CodeOutputWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message);
        this.multilineWidget = (new MultiLineTextWidget(message, font)).setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }
    public static final WidgetSprites ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"guantai"),Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"guantai"));

    public CodeOutputWidget setColor(int color) {
        this.multilineWidget.setColor(color);
        return this;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

    protected int getInnerHeight() {
        return this.multilineWidget.getHeight();
    }

    protected double scrollRate() {
        return 9.0F;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            if (!this.scrollbarVisible()) {
                this.renderBackground(guiGraphics);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(this.getX(), this.getY(), 0.0F);
                this.multilineWidget.render(guiGraphics, mouseX, mouseY, partialTick);
                guiGraphics.pose().popPose();
            } else {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((this.getX() + this.innerPadding()), (this.getY() + this.innerPadding()), 0.0F);
        this.multilineWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    public void setOutput(Deque<Component> output) {
        // STUB: going to change, for better performance in multiline widget,
        //  which split the line every time called
        MutableComponent newOutput = Component.empty();
        for (var component : output) {
            newOutput.append(component).append("\n");
        }
        multilineWidget.setMessage(newOutput);
        setScrollAmount(getMaxScrollAmount());
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.blitSprite(ENTRY_SPRITES.get(true,true), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
}
