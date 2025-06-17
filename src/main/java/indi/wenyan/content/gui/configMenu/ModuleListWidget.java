package indi.wenyan.content.gui.configMenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import indi.wenyan.content.gui.configMenu.BaseModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The Module list widget.
 */
public class ModuleListWidget extends ObjectSelectionList<ModuleListWidget.ModuleEntry> {

    /**
     * The modules screen.
     */
    final ModulesScreen modulesScreen;
    /**
     * The Module entries.
     */
    final List<ModuleEntry> moduleEntries = new ArrayList<>();

    /**
     * Instantiates a new Module list widget.
     *
     * @param modulesScreen the module screen
     * @param client        the Minecraft client
     * @param width         the width
     * @param height        the height
     * @param y             the y
     * @param itemHeight    the itemHeight
     */
    public ModuleListWidget(final ModulesScreen modulesScreen, final Minecraft client, final int width,
                            final int height, final int y, final int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.modulesScreen = modulesScreen;
    }

    /**
     * Gets scrollbar position x.
     *
     * @return the scrollbar position x
     */
    protected int scrollbarPositionX() {
        return super.getScrollbarPosition() + 30;
    }

    /**
     * Gets the row width.
     *
     * @return the row width
     */
    public int rowWidth() {
        return super.getRowWidth() + 85;
    }

    /**
     * The entry.
     *
     * @param index the index
     *
     * @return ModuleEntry
     */
    public ModuleEntry entry(final int index) {
        return this.moduleEntries.get(index);
    }

    /**
     * Sets modules.
     *
     * @param modules the modules
     */
    public void modules(final List<BaseModule> modules) {
        this.moduleEntries.clear();
        this.clearEntries();

        for (final BaseModule module : modules) {
            this.addModule(module);
        }
    }

    /**
     * Updates the modules.
     */
    public void updateModules() {
        this.clearEntries();
        this.moduleEntries.forEach(this::addEntry);

    }

    /**
     * Add a module.
     *
     * @param module the module
     */
    public void addModule(final BaseModule module) {
        final ModuleEntry entry = new ModuleEntry(this.modulesScreen, module);
        this.moduleEntries.add(entry);
        this.addEntry(entry);
    }

    /**
     * Remove a module.
     *
     * @param index the index of the module
     */
    public void removeModule(final int index) {
        final ModuleEntry entry = this.moduleEntries.get(index);
        this.moduleEntries.remove(entry);
        this.removeEntry(entry);
        this.modulesScreen.updateButtons();
        if (this.getScrollAmount() > this.getMaxScroll()) {
            this.setScrollAmount(this.getMaxScroll());
        }
        //BaseModule.modules.remove(index);
    }

    /**
     * A module entry.
     */
    public class ModuleEntry extends Entry<ModuleEntry> {
        private final ModulesScreen modulesScreen;
        private final Minecraft client;
        /**
         * The Module.
         */
        public final BaseModule module;

        /**
         * Instantiates a new Module entry.
         *
         * @param modulesScreen the module screen
         * @param module the module
         */
        protected ModuleEntry(final ModulesScreen modulesScreen, final BaseModule module) {
            this.modulesScreen = modulesScreen;
            this.module = module;
            this.client = Minecraft.getInstance();
        }

        // Fixes 1.17 crash
        @Override
        public Component getNarration() {
            return Component.nullToEmpty(this.module.toString());
        }

        /**
         * Renders the module list widget.
         *
         * @param context the Draw Context
         * @param index the entry index
         * @param y the y location
         * @param x the x location
         * @param entryWidth the row width
         * @param entryHeight the row height
         * @param mouseX the mouse x location
         * @param mouseY the mouse y location
         * @param hovered if the mouse is hovering
         * @param tickDelta the delta
         */
        public void render(final GuiGraphics context, final int index, final int y, final int x, final int entryWidth, final int entryHeight,
                           final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {

            context.drawString(this.client.font, this.module.toString(), x + 35, y + 1, 0xffffff, true);

            final Component exampleText;


            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (this.client.options.touchscreen().get() || hovered) {
                //RenderSystem.setShaderTexture(0, new Identifier("textures/gui/server_selection.png"));
                context.fill(x, y, x + 32, y + 32, -1601138544);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                final int v = mouseX - x;
                final int w = mouseY - y;

                if (index > 0) {
                    if (v < 16 && w < 16) {
                        context.blitSprite(ResourceLocation.parse("server_list/move_up_highlighted"), x, y, 32, 32);
                    } else {
                        context.blitSprite(ResourceLocation.parse("server_list/move_up"), x, y, 32, 32);
                    }
                }

                if (index < ModuleListWidget.this.moduleEntries.size() - 1) {
                    if (v < 16 && w > 16) {
                        context.blitSprite(ResourceLocation.parse("server_list/move_down_highlighted"), x, y, 32, 32);
                    } else {
                        context.blitSprite(ResourceLocation.parse("server_list/move_down"), x, y, 32, 32);
                    }
                }
            }
        }

        /**
         * Gets mouse clicked.
         *
         * @param mouseX the mouse x
         * @param mouseY the mouse y
         * @param button the button
         * @return if mouse clicked
         */
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            final double d = mouseX - (double) this.modulesScreen.modulesListWidget.getRowLeft();
            final double e =
                    mouseY - (double) ModuleListWidget.this.getRowTop(ModuleListWidget.this.children().indexOf(this));
            if (d <= 32.0D) {
                final int i = this.modulesScreen.modulesListWidget.children().indexOf(this);
                if (d < 16.0D && e < 16.0D && i > 0) {
                    this.swapEntries(i, i - 1);
                    return true;
                }

                if (d < 16.0D && e > 16.0D && i < ModuleListWidget.this.moduleEntries.size() - 1) {
                    this.swapEntries(i, i + 1);
                    return true;
                }
            }

            this.modulesScreen.select(this);
            return false;
        }

        private void swapEntries(final int i, final int j) {

            final ModuleEntry temp = ModuleListWidget.this.moduleEntries.get(i);

            ModuleListWidget.this.moduleEntries.set(i, ModuleListWidget.this.moduleEntries.get(j));
            ModuleListWidget.this.moduleEntries.set(j, temp);

            this.modulesScreen.modulesListWidget.setSelected(temp);
            this.modulesScreen.updateButtons();
            this.modulesScreen.modulesListWidget.updateModules();

        }
    }

}
