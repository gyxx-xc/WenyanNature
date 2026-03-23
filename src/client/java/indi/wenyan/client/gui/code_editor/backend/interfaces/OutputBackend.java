package indi.wenyan.client.gui.code_editor.backend.interfaces;

import net.minecraft.network.chat.Component;

import java.util.Deque;

public interface OutputBackend {
    Deque<Component> getOutput();

    void setOutputListener(java.util.function.Consumer<Deque<Component>> outputListener);
}
