package indi.wenyan.content.block.runner;

import net.minecraft.network.chat.Component;

import java.util.Deque;

public interface IOutputAccepter {
    void addOutput(String output, IOutputAccepter.OutputStyle style);

    Deque<Component> getOutputQueue();

    enum OutputStyle {
        NORMAL,
        ERROR
    }
}
