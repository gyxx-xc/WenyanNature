package indi.wenyan.judou.compiler;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;

public interface IWenyanBytecode {
    /**
     * Retrieves the bytecode instruction at the specified index.
     *
     * @param index The instruction index
     * @return The bytecode instruction
     */
    WenyanBytecode.Code get(int index);

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
     * Creates a new label in the label table.
     *
     * @return The index of the new label
     */
    int getNewLabel();

    /**
     * Retrieves debug context information for a given index.
     *
     * @param index The code index
     * @return The context information, or null if not found
     */
    WenyanBytecode.Context getContext(int index) throws WenyanException.WenyanVarException;

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
