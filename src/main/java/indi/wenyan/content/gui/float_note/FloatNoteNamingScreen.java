package indi.wenyan.content.gui.float_note;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * should be:
 * <p>
 * -----------------------
 * |  -----------------  |
 * | |   text field    | |
 * |  -----------------  |
 * |                     |
 * | (lock)    (confirm) |
 * -----------------------
 * */
public class FloatNoteNamingScreen extends Screen {
    private static final ResourceLocation TEXT_FIELD_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
    private EditBox name;
    private final Consumer<Component> save;

    public FloatNoteNamingScreen(Consumer<Component> save) {
        super(Component.empty());
        this.save = save;
    }

    @Override
    protected void init() {
        this.name = new EditBox(this.font, 62, 24, 103, 12,
                Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(18);
        this.name.setValue("");
        addRenderableWidget(name);

        Button confirmButton = Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(this.width / 2 + 4, 52, 50, 20)
                .tooltip(Tooltip.create(Component.empty()))
                .build();
        addRenderableWidget(confirmButton);

        Button lockButton = new LockIconButton(this.width / 2 - 54, 52, button -> {
            ((LockIconButton) button).setLocked(!((LockIconButton) button).isLocked());
            name.setEditable(!((LockIconButton) button).isLocked());
        });
        addRenderableWidget(lockButton);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(TEXT_FIELD_SPRITE, 59, 20, 110, 16);
        renderTransparentBackground(guiGraphics);
    }

    @Override
    public void onClose() {
        save.accept(Component.translatable("code.wenyan_programming.bracket", name.getValue()));
        super.onClose();
    }
}
