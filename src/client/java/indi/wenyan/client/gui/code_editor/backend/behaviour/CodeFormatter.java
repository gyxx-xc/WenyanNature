package indi.wenyan.client.gui.code_editor.backend.behaviour;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class CodeFormatter {
    // receive a string return the formatted string
    public static FormattedCharSequence highlightCode(String code) {
        return Component.literal("").getVisualOrderText();
    }
}
