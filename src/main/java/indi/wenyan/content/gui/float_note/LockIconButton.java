package indi.wenyan.content.gui.float_note;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@OnlyIn(Dist.CLIENT)
public class LockIconButton extends Button {
    private boolean locked;

    public LockIconButton(int x, int y, Button.OnPress onPress) {
        super(x, y, 20, 20,
                Component.empty(), onPress, DEFAULT_NARRATION);
        setTooltip(Tooltip.create(Component.translatable("gui.wenyan.lock")));
    }

    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Icon lockiconbutton$icon;
        if (!this.active) {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
        } else if (this.isHoveredOrFocused()) {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
        } else {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
        }

        guiGraphics.blitSprite(lockiconbutton$icon.sprite, this.getX(), this.getY(), this.width, this.height);
    }

    @OnlyIn(Dist.CLIENT)
    enum Icon {
        LOCKED(ResourceLocation.withDefaultNamespace("widget/locked_button")),
        LOCKED_HOVER(ResourceLocation.withDefaultNamespace("widget/locked_button_highlighted")),
        LOCKED_DISABLED(ResourceLocation.withDefaultNamespace("widget/locked_button_disabled")),
        UNLOCKED(ResourceLocation.withDefaultNamespace("widget/unlocked_button")),
        UNLOCKED_HOVER(ResourceLocation.withDefaultNamespace("widget/unlocked_button_highlighted")),
        UNLOCKED_DISABLED(ResourceLocation.withDefaultNamespace("widget/unlocked_button_disabled"));

        final ResourceLocation sprite;

        Icon(ResourceLocation sprite) {
            this.sprite = sprite;
        }
    }
}
