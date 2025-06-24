package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.executor.WenyanCode;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WenyanBytecode {
    private final List<Code> bytecode = new ArrayList<>();
    private final List<IWenyanValue> constTable = new ArrayList<>();
    private final List<String> identifierTable = new ArrayList<>();
    private final List<Integer> labelTable = new ArrayList<>();
    private final List<Context> debugTable = new ArrayList<>();

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
        return bytecode.get(index);
    }

    public IWenyanValue getConst(int index) {
        return constTable.get(index);
    }

    public int addConst(IWenyanValue value) {
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

    public void addContext(int line, int column, int start, int end, String content) {
        debugTable.add(new Context(line, column, start, end, content));
    }

    public Context getContext(int index) {
        // change to binary search
        for (Context context : debugTable) {
            if (context.start <= index && index < context.end) {
                return context;
            }
        }
        return null;
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
        int j = 0;
        for (int i = 0; i < bytecode.size(); i++) {
            if (j < debugTable.size() && i >= debugTable.get(j).start) {
                sb.append("Context: ").append(debugTable.get(j)).append("\n");
                j++;
            }
            sb.append(i).append(": ").append(bytecode.get(i)).append("\n");
        }
        return sb.toString();
    }

    public record Context(int line, int column, int start, int end, String content){}
}
