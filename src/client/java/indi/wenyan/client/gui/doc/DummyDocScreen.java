package indi.wenyan.client.gui.doc;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class DummyDocScreen extends Screen {
    public DummyDocScreen(Component title) {
        super(title);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(font, "施工中", 10, 10, 0xFFFFFFFF);
    }
}
