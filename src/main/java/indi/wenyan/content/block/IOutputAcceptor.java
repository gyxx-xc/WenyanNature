package indi.wenyan.content.block;

import net.minecraft.network.chat.Component;

import java.util.Deque;

public interface IOutputAcceptor {
    void addOutput(String output, IOutputAcceptor.OutputStyle style);

    Deque<Component> getOutputQueue();

    enum OutputStyle {
        NORMAL,
        ERROR
    }
}
