package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.executor.BranchCode;
import indi.wenyan.interpreter.runtime.executor.ForCode;
import indi.wenyan.interpreter.runtime.executor.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanException;

/**
 * Verifier for WenyanBytecode to ensure execution safety.
 */
public class WenyanVerifier {

    /**
     * Verifies the given bytecode.
     *
     * @param bytecode The bytecode to verify
     * @throws WenyanException if verification fails
     */
    public static void verify(WenyanBytecode bytecode) {
        int codeSize = bytecode.size();
        
        for (int i = 0; i < codeSize; i++) {
            WenyanBytecode.Code code = bytecode.get(i);
            WenyanCode op = code.code();
            int arg = code.arg();

            if (op instanceof BranchCode || op instanceof ForCode) {
                verifyLabel(bytecode, arg, i);
            }
        }
    }

    private static void verifyLabel(WenyanBytecode bytecode, int labelIndex, int instructionIndex) {
        try {
            // Check if label index is valid via getLabel and target PC is in valid bytecode range
            int targetPC = bytecode.getLabel(labelIndex);

            if (targetPC < 0 || targetPC > bytecode.size()) {
                throw new WenyanException("Verification failed at instr " + instructionIndex +
                        ": Jump target " + targetPC + " out of bounds [0, " + bytecode.size() + "]");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new WenyanException("Verification failed at instr " + instructionIndex +
                    ": Invalid label index " + labelIndex);
        }
    }
}
