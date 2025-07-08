package indi.wenyan.content.gui;

import indi.wenyan.WenyanProgramming;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextFieldScreen extends Screen {
    private String data;
    private final Consumer<String> saving;
    private final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
            "textures/gui/edit.png");

    public static final int WIDTH = 256;
    public static final int TEXT_FIELD_WIDTH = 226;

    public TextFieldScreen(String data, Consumer<String> save) {
        super(Component.empty());
        this.data = data;
        this.saving = save;
    }

    @Override
    protected void init() {
        TextFieldWidget textFieldWidget = new TextFieldWidget(font, (width - TEXT_FIELD_WIDTH) / 2, 15, TEXT_FIELD_WIDTH, 159);
        textFieldWidget.setValue(data);
        textFieldWidget.setValueListener(s -> data = s);
        addRenderableWidget(textFieldWidget);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND, (width - WIDTH) / 2, 2, 0, 0, WIDTH, 192);
    }

    @Override
    public void onClose() {
        saving.accept(data);
        super.onClose();
    }
}
