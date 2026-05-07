package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class WenyanImmutableBytecode implements IWenyanBytecode {
    private final int size;
    private final int[] codes;
    private final int[] args;
    private final int[] labelTable;
    private final String[] identifierTable;
    private final IWenyanValue[] constTable;

    @Getter
    private final List<WenyanBytecode.CapturedValue> capturedValues;
    private final List<WenyanBytecode.Context> debugTable;
    private final List<WenyanBytecode.ErrorHandlingContext> errorHandlingContexts;

    @Getter
    private final String sourceCode;

    public WenyanImmutableBytecode(int size, int[] codes, int[] args, IWenyanValue[] constTable, String[] identifierTable, int[] labelTable, List<WenyanBytecode.CapturedValue> capturedValues, List<WenyanBytecode.Context> debugTable, List<WenyanBytecode.ErrorHandlingContext> errorHandlingContexts, String sourceCode) {
        this.size = size;
        this.codes = codes;
        this.args = args;
        this.constTable = constTable;
        this.identifierTable = identifierTable;
        this.labelTable = labelTable;
        this.capturedValues = capturedValues;
        this.debugTable = debugTable;
        this.sourceCode = sourceCode;
        this.errorHandlingContexts = errorHandlingContexts;
    }

    @Override
    public WenyanCodes getCode(int index) {
        return WenyanCodes.values()[getCodeOrdinal(index)];
    }

    @Override
    public int getCodeOrdinal(int index) {
        return codes[index];
    }

    @Override
    public int getArg(int index) {
        return args[index];
    }

    @Override
    public IWenyanValue getConst(int index) {
        return constTable[index];
    }

    @Override
    public String getIdentifier(int index) {
        return identifierTable[index];
    }

    @Override
    public WenyanBytecode.@Nullable Context getContext(int index) {
        // change to binary search
        for (WenyanBytecode.Context context : debugTable) {
            if (context.bytecodeStart() <= index && index < context.bytecodeEnd()) {
                return context;
            }
        }
        return null;
    }

    @Override
    public int getErrorHandler(int index) {
        for (var context : errorHandlingContexts) {
            if (context.start() <= index && index < context.end()) {
                return context.pc();
            }
        }
        return -1;
    }

    @Override
    public int getLabel(int index) {
        return labelTable[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("constTable=").append(Arrays.toString(constTable)).append("\n");
        sb.append("identifierTable=").append(Arrays.toString(identifierTable)).append("\n");
        sb.append("labelTable=").append(Arrays.toString(labelTable)).append("\n");
        int j = 0;
        for (int i = 0; i < size(); i++) {
            if (j < debugTable.size() && i >= debugTable.get(j).bytecodeStart()) {
                sb.append("Context: ").append(debugTable.get(j)).append("\n");
                j++;
            }
            sb.append(i).append(": ").append(getCode(i)).append("\n");
        }
        return sb.toString();
    }
}
