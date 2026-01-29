package indi.wenyan.content.gui.code_editor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;


// copy from FittingMultiLineTextWidget
@OnlyIn(Dist.CLIENT)
public class CodeOutputWidget extends AbstractScrollWidget {
    private final MultiLineTextWidget multilineWidget;

    public CodeOutputWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message);
        this.multilineWidget = (new MultiLineTextWidget(message, font)).setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

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
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if (this.scrollbarVisible()) {
            super.renderBackground(guiGraphics);
        } else if (this.isFocused()) {
            this.renderBorder(guiGraphics, this.getX() - this.innerPadding(), this.getY() - this.innerPadding(), this.getWidth() + this.totalInnerPadding(), this.getHeight() + this.totalInnerPadding());
        }

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

    public void addOutput(List<Component> output) {
        // STUB: going to change
        String newOutput = output.stream()
                .map(Component::getString)
                .reduce("", (a, b) -> a + b + "\n");
        multilineWidget.setMessage(Component.literal(multilineWidget.getMessage().getString() + newOutput));
        setScrollAmount(getMaxScrollAmount());
    }
}
