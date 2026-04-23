package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    WenyanBytecode.@Nullable Context getContext(int index);

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

    List<WenyanBytecode.CapturedValue> getCapturedValues();

    String getSourceCode();

    IWenyanBytecode EMPTY = new IWenyanBytecode() {
        @Override
        public WenyanCodes getCode(int index) {
            return WenyanCodes.BREAKPOINT;
        }

        @Override
        public int getCodeOrdinal(int index) {
            return 0;
        }

        @Override
        public int getArg(int index) {
            return 0;
        }

        @Override
        public IWenyanValue getConst(int index) {
            return WenyanNull.NULL;
        }

        @Override
        public String getIdentifier(int index) {
            return "";
        }

        @Override
        public WenyanBytecode.@Nullable Context getContext(int index) {
            return null;
        }

        @Override
        public int getLabel(int index) {
            return 0;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public List<WenyanBytecode.CapturedValue> getCapturedValues() {
            return List.of();
        }

        @Override
        public String getSourceCode() {
            return "";
        }
    };
}
