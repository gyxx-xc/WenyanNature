package indi.wenyan.content.gui.configMenu;
import java.util.Objects;
import indi.wenyan.content.gui.configMenu.ModConfigFile;
import indi.wenyan.content.gui.configMenu.BaseModule;
import indi.wenyan.content.gui.configMenu.PositionEnum;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The Modules screen.
 */
public class ModulesScreen extends Screen {

    /**
     * The parent screen.
     */
    final Screen parent;
    /**
     * The Modules list widget.
     */
    ModuleListWidget modulesListWidget;
    private boolean initialized = false;

    private Button editButton;
    private Button deleteButton;

    /**
     * The side of the screen (left or right).
     */
    public final PositionEnum side;

    /**
     * Instantiates a new Modules screen.
     *
     * @param parent the parent screen
     * @param side   the side of the screen
     */
    public ModulesScreen(final Screen parent, final PositionEnum side) {
        super(Component.translatable("config.betterf3.title.modules"));
        this.parent = parent;
        this.side = side;

    }

    @Override
    protected void init() {
        super.init();

        if (this.initialized) {
            this.modulesListWidget.setRectangle(this.width, this.height - 64 - 32, 0, 32);
            this.modulesListWidget.updateModules();
        } else {
            this.initialized = true;
            this.modulesListWidget = new ModuleListWidget(this, this.minecraft, this.width, this.height - 64 - 32, 32, 36);
            if (this.side == PositionEnum.LEFT) {
                this.modulesListWidget.modules(BaseModule.modules);
            } else if (this.side == PositionEnum.RIGHT) {
                this.modulesListWidget.modules(BaseModule.modulesRight);
            }
        }

        this.addRenderableWidget(this.modulesListWidget);

        final Button editButton = Button.builder(Component.translatable("config.betterf3.modules.edit_button"),
                        button -> {
                            final Screen screen = EditModulesScreen.configBuilder(Objects.requireNonNull(this.modulesListWidget.getSelected()).module, this).build();
                            assert minecraft != null;
                            minecraft.setScreen(screen);
                        })
                .bounds(this.width / 2 - 50, this.height - 50, 100, 20).build();
        this.editButton = this.addRenderableWidget(editButton);

        final Button addButton = Button.builder(Component.translatable("config.betterf3.modules.add_button"),
                        button -> {
                            assert minecraft != null;
//                            minecraft.setScreen(AddModuleScreen.configBuilder(this).build());
                        })
                .bounds(this.width / 2 + 4 + 50, this.height - 50, 100, 20).build();
        this.addRenderableWidget(addButton);

        final Button deleteButton = Button.builder(Component.translatable("config.betterf3.modules.delete_button"),
                        button -> this.modulesListWidget.removeModule(this.modulesListWidget.moduleEntries.indexOf(Objects.requireNonNull(this.modulesListWidget.getSelected()))))
                .bounds(this.width / 2 - 154, this.height - 50, 100, 20).build();
        this.deleteButton = this.addRenderableWidget(deleteButton);

        final Button doneButton = Button.builder(Component.translatable("config.betterf3.modules.done_button"),
                        button -> {
                            this.onClose();
                            assert minecraft != null;
                            minecraft.setScreen(this.parent);
                        })
                .bounds(this.width / 2 - 154, this.height - 30 + 4, 308, 20).build();
        this.addRenderableWidget(doneButton);

        this.updateButtons();

    }

    @Override
    public void render(final GuiGraphics context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.modulesListWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if (this.side == PositionEnum.LEFT) {
            BaseModule.modules.clear();
            for (final ModuleListWidget.ModuleEntry entry : this.modulesListWidget.moduleEntries) {
                BaseModule.modules.add(entry.module);
            }
        } else if (this.side == PositionEnum.RIGHT) {
            BaseModule.modulesRight.clear();
            for (final ModuleListWidget.ModuleEntry entry : this.modulesListWidget.moduleEntries) {
                BaseModule.modulesRight.add(entry.module);
            }
        }
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
        ModConfigFile.saveRunnable.run();
    }

    /**
     * Selects a module.
     *
     * @param entry the entry
     */
    public void select(final ModuleListWidget.ModuleEntry entry) {
        this.modulesListWidget.setSelected(entry);
        this.updateButtons();
    }

    /**
     * Updates the buttons.
     */
    public void updateButtons() {
        if (this.modulesListWidget.getSelected() != null) {
            this.editButton.active = true;
            this.deleteButton.active = true;
        } else {
            this.editButton.active = false;
            this.deleteButton.active = false;
        }
    }

}
