package indi.wenyan.client.gui.code_editor.backend.interfaces;

import net.minecraft.network.chat.Component;

import java.util.Deque;

public interface OutputSynchronizer {
    Deque<Component> getOutput();

    boolean isOutputChanged();
}
