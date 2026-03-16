package indi.wenyan.content.block.runner;

import indi.wenyan.setup.network.PlatformOutputPacket;
import net.minecraft.network.chat.Component;

import java.util.Deque;

public interface ICodeOutputHolder extends ICodeHolder {
    boolean isOutputChanged();

    void addOutput(String output, PlatformOutputPacket.OutputStyle style);

    Deque<Component> getOutputQueue();
}
