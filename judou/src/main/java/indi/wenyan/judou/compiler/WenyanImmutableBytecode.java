package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Getter;

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

    @Getter
    private final String sourceCode;

    public WenyanImmutableBytecode(int size, int[] codes, int[] args, IWenyanValue[] constTable, String[] identifierTable, int[] labelTable, List<WenyanBytecode.CapturedValue> capturedValues, List<WenyanBytecode.Context> debugTable, String sourceCode) {
        this.size = size;
        this.codes = codes;
        this.args = args;
        this.constTable = constTable;
        this.identifierTable = identifierTable;
        this.labelTable = labelTable;
        this.capturedValues = capturedValues;
        this.debugTable = debugTable;
        this.sourceCode = sourceCode;
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
    public WenyanBytecode.Context getContext(int index) {
        return debugTable.get(index);
    }

    @Override
    public int getLabel(int index) {
        return labelTable[index];
    }

    @Override
    public int size() {
        return size;
    }
}
