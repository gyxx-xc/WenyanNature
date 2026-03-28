package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.IWenyanValue;

public interface IWenyanBytecode {
    WenyanCodes getCode(int index);

    int getCodeOrdinal(int index);

    int getArg(int index);

    /**
     * Retrieves a constant value from the constant table.
     *
     * @param index The constant index
     * @return The constant value
     */
    IWenyanValue getConst(int index);

    /**
     * Retrieves an identifier from the identifier table.
     *
     * @param index The identifier index
     * @return The identifier string
     */
    String getIdentifier(int index);

    /**
     * Retrieves debug context information for a given index.
     *
     * @param index The code index
     * @return The context information, or null if not found
     * @throws IndexOutOfBoundsException If the identifier is not found
     */
    WenyanBytecode.Context getContext(int index);

    /**
     * Gets the label value at the specified index.
     *
     * @param index The label index
     * @return The label value
     */
    int getLabel(int index);

    /**
     * Returns the size of the bytecode.
     *
     * @return Number of bytecode instructions
     */
    int size();

    java.util.List<WenyanBytecode.CapturedValue> getCapturedValues();

    String getSourceCode();
}
