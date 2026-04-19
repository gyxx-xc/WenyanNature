package indi.wenyan.judou.compiler;

import indi.wenyan.judou.compiler.visitor.WenyanVisitor;

import java.util.Collections;
import java.util.List;

public enum WenyanCompiler {;
    public static BytecodeWithExportedValues compile(String sourceCode) {
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(sourceCode, null, Collections.emptyList());

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

    public record BytecodeWithExportedValues(IWenyanBytecode bytecode, List<String> exportedValues) {
    }
}
