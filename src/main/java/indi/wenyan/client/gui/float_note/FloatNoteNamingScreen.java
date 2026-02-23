package indi.wenyan.client.gui.float_note;

import indi.wenyan.setup.definitions.WYRegistration;
import indi.wenyan.setup.network.FloatNotePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.Consumer;

public class FloatNoteNamingScreen extends Screen {
    private static final Identifier TEXT_FIELD_SPRITE = Identifier.withDefaultNamespace("container/anvil/text_field");
    private EditBox name;
    private final ItemStack item;
    private final Consumer<Component> save;

    public FloatNoteNamingScreen(Consumer<Component> save, ItemStack item) {
        super(Component.empty());
        this.save = save;
        this.item = item;
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
        this.name.setValue(item.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString());
        addRenderableWidget(name);

        Button confirmButton = Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(this.width / 2 + 4, 52, 50, 20)
                .tooltip(Tooltip.create(Component.empty()))
                .build();
        addRenderableWidget(confirmButton);

        LockIconButton lockButton = new LockIconButton(this.width / 2 - 54, 52, button -> {
            button.setLocked(!button.isLocked());
            name.setEditable(!button.isLocked());
            item.set(WYRegistration.NOTE_LOCK_DATA.get(), button.isLocked());
            ClientPacketDistributor.sendToServer(new FloatNotePacket(name.getValue(), button.isLocked()));
        });
        lockButton.setLocked(item.getOrDefault(WYRegistration.NOTE_LOCK_DATA.get(), false));
        addRenderableWidget(lockButton);

        name.setResponder(text -> {
            if (text.equals(StringUtil.filterText(text))) {
                item.set(DataComponents.CUSTOM_NAME, Component.literal(text));
                ClientPacketDistributor.sendToServer(new FloatNotePacket(text, lockButton.isLocked()));
            }
        });
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXT_FIELD_SPRITE, 59, 20, 110, 16);
        renderTransparentBackground(guiGraphics);
    }

    @Override
    public void onClose() {
        save.accept(Component.translatable("code.wenyan_programming.bracket", name.getValue()));
        super.onClose();
    }
}
