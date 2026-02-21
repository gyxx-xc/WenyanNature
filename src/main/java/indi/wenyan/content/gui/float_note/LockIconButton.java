package indi.wenyan.content.gui.float_note;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LockIconButton extends Button {
    @Setter @Getter
    private boolean locked;

    public LockIconButton(int x, int y, OnPress onPress) {
        super(x, y, 20, 20,
                Component.empty(), button -> onPress.onPress((LockIconButton) button),
                DEFAULT_NARRATION);
        setTooltip(Tooltip.create(Component.translatable("gui.wenyan.lock")));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Icon icon;
        if (!this.active) {
            icon = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
        } else if (this.isHoveredOrFocused()) {
            icon = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
        } else {
            icon = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
        }

        guiGraphics.blitSprite(icon.sprite, this.getX(), this.getY(), this.width, this.height);
    }

    @OnlyIn(Dist.CLIENT)
    enum Icon {
        LOCKED(Identifier.withDefaultNamespace("widget/locked_button")),
        LOCKED_HOVER(Identifier.withDefaultNamespace("widget/locked_button_highlighted")),
        LOCKED_DISABLED(Identifier.withDefaultNamespace("widget/locked_button_disabled")),
        UNLOCKED(Identifier.withDefaultNamespace("widget/unlocked_button")),
        UNLOCKED_HOVER(Identifier.withDefaultNamespace("widget/unlocked_button_highlighted")),
        UNLOCKED_DISABLED(Identifier.withDefaultNamespace("widget/unlocked_button_disabled"));

        final Identifier sprite;

        Icon(Identifier sprite) {
            this.sprite = sprite;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(LockIconButton var1);
    }
}
