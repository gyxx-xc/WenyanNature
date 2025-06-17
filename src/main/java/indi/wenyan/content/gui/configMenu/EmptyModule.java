package indi.wenyan.content.gui.configMenu;

import indi.wenyan.content.gui.configMenu.BaseModule;
import net.minecraft.client.Minecraft;

/**
 * An Empty module.
 */
public class EmptyModule extends BaseModule {

    /**
     * The number of empty lines to display.
     */
    public int emptyLines;

    /**
     * The number of lines displayed to the client.
     */
    public int displayedLines;

    /**
     * The default number of empty lines to display.
     */
    public final int defaultEmptyLines = 1;

    /**
     * Instantiates a new Empty module.
     *
     * @param invisible sets invisibility
     */
    public EmptyModule(final boolean invisible) {
        super(invisible);

        this.emptyLines = this.defaultEmptyLines;
        this.displayedLines = this.emptyLines;


    }

    @Override
    public void update(final Minecraft client) {
        final int loopLines = this.emptyLines != this.displayedLines ? 20 : this.displayedLines;

        if (loopLines == 20) {
            if (this.emptyLines > 20) {
                this.emptyLines = 20;
            }
            this.displayedLines = this.emptyLines;
        }
    }
}
