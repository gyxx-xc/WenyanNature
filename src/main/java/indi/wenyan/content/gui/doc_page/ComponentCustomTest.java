package indi.wenyan.content.gui.doc_page;

import indi.wenyan.content.gui.code_editor.CodeEditorWidget;
import indi.wenyan.content.gui.code_editor.CodeField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class ComponentCustomTest implements ICustomComponent {
    private transient StringBuilder code;
    private transient CodeDisplayWidget textFieldWidget;
    private int width, height;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        int textFieldWidth = Mth.clamp(width, 50, CodeEditorWidget.WIDTH);
        textFieldWidget = new CodeDisplayWidget(Minecraft.getInstance().font, new CodeField.SavedVariable() {
            @Override
            public List<CodeField.Placeholder> getPlaceholders() {
                return List.of();
            }

            @Override
            public StringBuilder getContent() {
                return code;
            }

            @Override
            public int getCursor() {
                return 0;
            }

            @Override
            public void setCursor(int cursor) {

            }

            @Override
            public int getSelectCursor() {
                return 0;
            }

            @Override
            public void setSelectCursor(int selectCursor) {

            }

            @Override
            public boolean isSelecting() {
                return false;
            }

            @Override
            public void setSelecting(boolean selecting) {

            }
        },
                componentX, componentY, textFieldWidth, Math.min(height, CodeEditorWidget.HEIGH));
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pTicks,
                       int mouseX, int mouseY) {
        textFieldWidget.render(graphics);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        code = new StringBuilder(lookup.apply(IVariable.wrap("#code#", registries)).asString());
    }
}
