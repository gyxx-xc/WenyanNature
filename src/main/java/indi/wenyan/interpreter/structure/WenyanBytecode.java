package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WenyanBytecode extends WenyanProgramCode {
    private final List<Code> bytecode = new ArrayList<>();
    private final List<WenyanValue> constTable = new ArrayList<>();
    private final List<String> identifierTable = new ArrayList<>();
    private final List<Integer> labelTable = new ArrayList<>();
    private boolean changed = false;
    private int changedIndex = -1;
    private Code changedCode = null;

    public record Code(WenyanCode code, int arg) {
        @Override
        public @NotNull String toString() {
            return code+" "+arg;
        }
    }

    public void add(WenyanCode code, int arg) {
        bytecode.add(new Code(code, arg));
    }

    public Code get(int index) {
        if (changed && index == changedIndex) {
            changed = false;
            changedIndex = -1;
            return changedCode;
        }
        return bytecode.get(index);
    }

    public void setTemp(int index, WenyanCode code, int arg) {
        if (changed) {
            throw new WenyanException(Component.translatable("wenyan.error.bytecode.temp_change").getString());
        }
        changed = true;
        changedIndex = index;
        changedCode = new Code(code, arg);
    }

    public WenyanValue getConst(int index) {
        return constTable.get(index);
    }

    public int addConst(WenyanValue value) {
        constTable.add(value);
        return constTable.size() - 1;
    }

    public String getIdentifier(int index) {
        return identifierTable.get(index);
    }

    public int addIdentifier(String identifier) {
        identifierTable.add(identifier);
        return identifierTable.size() - 1;
    }

    public int getNewLabel() {
        labelTable.add(0);
        return labelTable.size() - 1;
    }

    public int getLabel(int index) {
        return labelTable.get(index);
    }

    public void setLabel(int index, int label) {
        labelTable.set(index, label);
    }

    public int size() {
        return bytecode.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("constTable=").append(constTable).append("\n");
        sb.append("identifierTable=").append(identifierTable).append("\n");
        sb.append("labelTable=").append(labelTable).append("\n");
        for (int i = 0; i < bytecode.size(); i++) {
            sb.append(i).append(": ").append(bytecode.get(i)).append("\n");
        }
        return sb.toString();
    }
}
