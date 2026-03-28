package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.BranchCode;
import indi.wenyan.judou.runtime.executor.ForCode;
import indi.wenyan.judou.runtime.executor.WenyanCode;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.utils.language.JudouExceptionText;

/**
 * Verifier for WenyanBytecode to ensure execution safety.
 */
// PLAN: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.10
public class WenyanVerifier {

    /**
     * Verifies the given bytecode.
     *
     * @param bytecode The bytecode to verify
     * @throws WenyanCompileException if verification fails
     */
    public static void verify(WenyanBytecode bytecode) throws WenyanCompileException {
        int codeSize = bytecode.size();
        
        for (int i = 0; i < codeSize; i++) {
            WenyanBytecode.Code code = bytecode.get(i);
            WenyanCode op = code.code().getCode();
            int arg = code.arg();

            if (op instanceof BranchCode || op instanceof ForCode) {
                verifyLabel(bytecode, arg);
            }
        }
    }

    private static void verifyLabel(WenyanBytecode bytecode, int labelIndex) throws WenyanCompileException {
        try {
            // Check if label index is valid via getLabel and target PC is in valid bytecode range
            int targetPC = bytecode.getLabel(labelIndex);

            if (targetPC < 0 || targetPC > bytecode.size()) {
                throw new WenyanCompileException(JudouExceptionText.VerificationFailed.string());
            }
        } catch (IndexOutOfBoundsException e) {
            throw new WenyanCompileException(JudouExceptionText.VerificationFailed.string());
        }
    }
}
