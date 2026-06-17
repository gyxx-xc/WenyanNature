package indi.wenyan.judou.api.compile;

import indi.wenyan.judou.api.values.exception.WenyanCompileException;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanPreprocessor;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;

import java.util.Collections;
import java.util.List;

/// Helper class that compiles Wenyan source code into executable bytecode.
public final class WenyanCompiler {
    private final boolean debug;

    public WenyanCompiler(boolean debug) {
        this.debug = debug;
    }

    public WenyanCompiler() {
        this(false);
    }

    /// Compiles Wenyan source code into bytecode.
    ///
    /// @param sourceCode the Wenyan source code to compile
    /// @return a record containing the compiled bytecode and exported variable names
    /// @throws WenyanCompileException if compilation fails
    public BytecodeWithExportedValues compile(String sourceCode) {
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(sourceCode, null, Collections.emptyList(), debug);

        // preprocess
        var preprocessedCode = WenyanPreprocessor.preprocess(sourceCode);

        // Intermediate Code Generation
        WenyanVisitor.generateTo(preprocessedCode, environment);
        var bytecode = environment.produceBytecode();
        List<String> exportedIdentifier = environment.getExportedValues();

        // TODO: optimize

        // Verification
        WenyanVerifier.verify(bytecode);

        return new BytecodeWithExportedValues(bytecode, exportedIdentifier);
    }

    /// Result of compiling Wenyan source code.
    ///
    /// @param bytecode      the compiled bytecode
    /// @param exportedValues list of variable names that are exported, for package import
    public record BytecodeWithExportedValues(IWenyanBytecode bytecode,
                                             List<String> exportedValues) {
    }
}
