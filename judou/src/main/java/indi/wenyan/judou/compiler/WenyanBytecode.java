package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the bytecode structure for the Wenyan interpreter.
 * Manages code instructions, constant values, identifiers, labels, and debug information.
 */
public class WenyanBytecode {
    private final List<Code> bytecode = new ArrayList<>();
    private final List<IWenyanValue> constTable = new ArrayList<>();
    private final List<String> identifierTable = new ArrayList<>();
    private final List<Integer> labelTable = new ArrayList<>();
    @Getter
    private final List<CapturedValue> capturedValues = new ArrayList<>();
    private final List<Context> debugTable = new ArrayList<>();
    @Getter
    private final String sourceCode;

    public WenyanBytecode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Adds a new bytecode instruction.
     *
     * @param code The operation code
     * @param arg  The argument value
     */
    public void add(WenyanCodes code, int arg) {
        bytecode.add(new Code(code, arg));
    }

    /**
     * Adds a value to the constant table.
     *
     * @param value The value to add
     * @return The index of the added constant
     */
    public int addConst(IWenyanValue value) {
        constTable.add(value);
        return constTable.size() - 1;
    }

    /**
     * Adds an identifier to the identifier table.
     *
     * @param identifier The identifier to add
     * @return The index of the added identifier
     */
    public int addIdentifier(String identifier) {
        identifierTable.add(identifier);
        return identifierTable.size() - 1;
    }

    public int getNewLabel() {
        labelTable.add(0);
        return labelTable.size() - 1;
    }

    /**
     * Adds debug context information.
     *
     * @param line         Line number
     * @param column       Column number
     * @param start        Start index
     * @param end          End index
     * @param contentStart Start index of source content
     * @param contentEnd   End index of source content
     */
    public void addContext(int line, int column, int start, int end, int contentStart, int contentEnd) {
        debugTable.add(new Context(line, column, start, end, contentStart, contentEnd));
    }

    /**
     * Sets a label value at the specified index.
     *
     * @param index The label index
     * @param label The label value
     */
    public void setLabel(int index, int label) {
        labelTable.set(index, label);
    }

    public int addCapturedValue(CapturedValue value) {
        capturedValues.add(value);
        return capturedValues.size() - 1;
    }

    public int size() {
        return bytecode.size();
    }

    public IWenyanBytecode toImmutable() {
        return new WenyanImmutableBytecode(
                size(),
                bytecode.stream().map(Code::code).mapToInt(WenyanCodes::ordinal).toArray(),
                bytecode.stream().mapToInt(Code::arg).toArray(),
                constTable.toArray(new IWenyanValue[0]),
                identifierTable.toArray(new String[0]),
                labelTable.stream().mapToInt(Integer::intValue).toArray(),
                capturedValues,
                debugTable,
                sourceCode
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("constTable=").append(constTable).append("\n");
        sb.append("identifierTable=").append(identifierTable).append("\n");
        sb.append("labelTable=").append(labelTable).append("\n");
        int j = 0;
        for (int i = 0; i < bytecode.size(); i++) {
            if (j < debugTable.size() && i >= debugTable.get(j).bytecodeStart) {
                sb.append("Context: ").append(debugTable.get(j)).append("\n");
                j++;
            }
            sb.append(i).append(": ").append(bytecode.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Represents a bytecode instruction with its operation code and argument.
     */
    public record Code(WenyanCodes code, int arg) {
        @Override
        public @NotNull String toString() {
            return code + " " + arg;
        }
    }

    /**
     * Represents debug context information for a segment of bytecode.
     */
    public record Context(int line, int column,
                          int bytecodeStart, int bytecodeEnd,
                          int contentStart, int contentEnd) {
    }

    public record CapturedValue(int index, boolean fromLocal) {
    }
}
