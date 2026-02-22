package indi.wenyan.content.gui.code_editor.widget;

import indi.wenyan.WenyanProgramming;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Deque;


// copy from FittingMultiLineTextWidget
@OnlyIn(Dist.CLIENT)
public class CodeOutputWidget extends AbstractTextAreaWidget {
    private final MultiLineTextWidget multilineWidget;
    //    public static Identifier BACKGROUND = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,"guantai.png");
    private int imageWidth = 256;
    private int imageHeight = 142;

    public CodeOutputWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message, AbstractScrollArea.defaultSettings(9));
        this.multilineWidget = (new MultiLineTextWidget(message, font)).setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

    public static final WidgetSprites ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "guantai"), Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "guantai"));

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

    protected int getInnerHeight() {
        return this.multilineWidget.getHeight();
    }

    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(getInnerLeft(), getInnerTop());
        this.multilineWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popMatrix();
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
        setScrollAmount(maxScrollAmount());
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENTRY_SPRITES.get(true, true), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
}
