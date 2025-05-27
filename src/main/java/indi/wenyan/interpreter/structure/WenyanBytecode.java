package indi.wenyan.interpreter.structure;

import java.util.ArrayList;
import java.util.List;

public class WenyanBytecode extends WenyanProgramCode {
    private final List<Code> bytecode = new ArrayList<>();
    private final List<WenyanValue> constTable = new ArrayList<>();
    private final List<String> identifierTable = new ArrayList<>();
    private final List<Integer> labelTable = new ArrayList<>();

    public void setArg(int index, int arg) {
        if (index < 0 || index >= bytecode.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        Code code = bytecode.get(index);
        bytecode.set(index, new Code(code.code, arg));
    }

    private record Code(indi.wenyan.interpreter.utils.WenyanCode code, int arg) {
        @Override
        public String toString() {
            return code+" "+arg;
        }
    }

    public void add(indi.wenyan.interpreter.utils.WenyanCode code, int arg) {
        bytecode.add(new Code(code, arg));
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
        return "bytecode=" + bytecode +
                "\nconstTable=" + constTable +
                "\nidentifierTable=" + identifierTable;
    }
}
