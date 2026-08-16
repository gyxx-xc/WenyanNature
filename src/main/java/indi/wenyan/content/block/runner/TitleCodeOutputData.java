package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.ICodeOutputHolder;
import indi.wenyan.content.block.IOutputAcceptor;
import indi.wenyan.setup.definitions.WyRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

import static indi.wenyan.content.block.runner.RunnerBlockEntity.PAGES_ID;
import static indi.wenyan.content.block.runner.RunnerBlockEntity.PLATFORM_NAME_ID;

public class TitleCodeOutputData implements ICodeOutputHolder {
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    @Getter private String code;
    @Getter private String platformName;
    @Getter private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;
    @Setter @Nullable private Runnable onChanged = null;

    public TitleCodeOutputData(String code, String platformName) {
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
    public void addOutput(String output, IOutputAcceptor.OutputStyle style) {
        if (style == IOutputAcceptor.OutputStyle.ERROR)
            outputQueue.addLast(Component.literal(output).withStyle(ChatFormatting.RED));
        else if (style == IOutputAcceptor.OutputStyle.NORMAL)
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

    public void applyImplicitComponents(DataComponentGetter components) {
        setCode(components.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), ""));
        setPlatformName(components.getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(platformName)).getString());
    }

    public void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(WyRegistration.PROGRAM_CODE_DATA.get(), code);
        components.set(DataComponents.CUSTOM_NAME, Component.literal(platformName));
    }

    public void saveData(ValueOutput tag) {
        tag.putString(PAGES_ID, code);
        tag.putString(PLATFORM_NAME_ID, platformName);
    }

    public void loadData(ValueInput tag) {
        tag.getString(PAGES_ID).ifPresent(this::setCode);
        tag.getString(PLATFORM_NAME_ID).ifPresent(this::setPlatformName);
    }
}
