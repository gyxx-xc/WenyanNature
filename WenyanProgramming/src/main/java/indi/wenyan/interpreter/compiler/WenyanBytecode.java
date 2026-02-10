package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.executor.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
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
    private final List<Context> debugTable = new ArrayList<>();

    /**
     * Represents a bytecode instruction with its operation code and argument.
     */
    public record Code(WenyanCode code, int arg) {
        @Override
        public @NotNull String toString() {
            return code+" "+arg;
        }
    }

    /**
     * Adds a new bytecode instruction.
     * @param code The operation code
     * @param arg The argument value
     */
    public void add(WenyanCode code, int arg) {
        bytecode.add(new Code(code, arg));
    }

    /**
     * Retrieves the bytecode instruction at the specified index.
     * @param index The instruction index
     * @return The bytecode instruction
     */
    public Code get(int index) {
        return bytecode.get(index);
    }

    /**
     * Retrieves a constant value from the constant table.
     * @param index The constant index
     * @return The constant value
     */
    public IWenyanValue getConst(int index) {
        return constTable.get(index);
    }

    /**
     * Adds a value to the constant table.
     * @param value The value to add
     * @return The index of the added constant
     */
    public int addConst(IWenyanValue value) {
        constTable.add(value);
        return constTable.size() - 1;
    }

    /**
     * Retrieves an identifier from the identifier table.
     * @param index The identifier index
     * @return The identifier string
     */
    public String getIdentifier(int index) {
        return identifierTable.get(index);
    }

    /**
     * Adds an identifier to the identifier table.
     * @param identifier The identifier to add
     * @return The index of the added identifier
     */
    public int addIdentifier(String identifier) {
        identifierTable.add(identifier);
        return identifierTable.size() - 1;
    }

    /**
     * Creates a new label in the label table.
     * @return The index of the new label
     */
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
     * Retrieves debug context information for a given index.
     * @param index The code index
     * @return The context information, or null if not found
     */
    public Context getContext(int index) throws WenyanException.WenyanVarException {
        // change to binary search
        for (Context context : debugTable) {
            if (context.bytecodeStart <= index && index < context.bytecodeEnd) {
                return context;
            }
        }
        throw new WenyanException.WenyanVarException("No debug info for index " + index);
    }

    /**
     * Gets the label value at the specified index.
     * @param index The label index
     * @return The label value
     */
    public int getLabel(int index) {
        return labelTable.get(index);
    }

    /**
     * Sets a label value at the specified index.
     * @param index The label index
     * @param label The label value
     */
    public void setLabel(int index, int label) {
        labelTable.set(index, label);
    }

    /**
     * Returns the size of the bytecode.
     * @return Number of bytecode instructions
     */
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
            if (j < debugTable.size() && i >= debugTable.get(j).bytecodeStart) {
                sb.append("Context: ").append(debugTable.get(j)).append("\n");
                j++;
            }
            sb.append(i).append(": ").append(bytecode.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Represents debug context information for a segment of bytecode.
     */
    public record Context(int line, int column,
                          int bytecodeStart, int bytecodeEnd,
                          int contentStart, int contentEnd){}
}
