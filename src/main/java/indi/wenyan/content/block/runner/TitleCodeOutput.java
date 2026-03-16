package indi.wenyan.content.block.runner;

import indi.wenyan.setup.network.PlatformOutputPacket;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

public class TitleCodeOutput implements ICodeOutputHolder {
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    @Getter private String code;
    @Getter private String platformName;
    @Getter private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;
    @Setter @Nullable private Runnable onChanged = null;

    public TitleCodeOutput(String code, String platformName) {
        this.code = code;
        this.platformName = platformName;
    }

    @Override
    public boolean isOutputChanged() {
        var temp = outputChanged;
        outputChanged = false;
        return temp;
    }

    @Override
    public void addOutput(String output, PlatformOutputPacket.OutputStyle style) {
        if (style == PlatformOutputPacket.OutputStyle.ERROR)
            outputQueue.addLast(Component.literal(output).withStyle(ChatFormatting.RED));
        else if (style == PlatformOutputPacket.OutputStyle.NORMAL)
            outputQueue.addLast(Component.literal(output));
        while (outputQueue.size() > MAX_OUTPUT_SHOWING_SIZE) {
            outputQueue.removeFirst();
        }
        outputChanged = true;
        if (onChanged != null)
            onChanged.run();
    }

    @Override
    public void setCode(String code) {
        this.code = code;
        if (onChanged != null)
            onChanged.run();
    }

    @Override
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
        if (onChanged != null)
            onChanged.run();
    }
}
