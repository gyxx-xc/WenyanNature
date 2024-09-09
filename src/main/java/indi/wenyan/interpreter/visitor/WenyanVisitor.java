package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public abstract class WenyanVisitor extends WenyanRBaseVisitor<WenyanValue> {
    protected WenyanFunctionEnvironment functionEnvironment;

    public WenyanVisitor(WenyanFunctionEnvironment functionEnvironment) {
        this.functionEnvironment = functionEnvironment;
    }

    public WenyanValue run(String s) {
        return visit(new WenyanRParser(
                new CommonTokenStream(
                        new WenyanRLexer(
                                CharStreams.fromString(s)))).program()
        );
    }
}
