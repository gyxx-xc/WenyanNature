package indi.wenyan.judou.api.compile;

import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanPreprocessor;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;

import java.util.Collections;
import java.util.List;

public final class WenyanCompiler {
    private final boolean debug;

    public WenyanCompiler(boolean debug) {
        this.debug = debug;
    }

    public WenyanCompiler() {
        this(false);
    }

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

    public record BytecodeWithExportedValues(IWenyanBytecode bytecode,
                                             List<String> exportedValues) {
    }
}
