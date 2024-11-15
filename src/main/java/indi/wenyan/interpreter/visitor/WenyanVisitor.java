package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanErrorListener;
import indi.wenyan.interpreter.structure.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.structure.WenyanValue;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public abstract class WenyanVisitor extends WenyanRBaseVisitor<WenyanValue> {
    protected final WenyanControl control;
    protected final WenyanFunctionEnvironment functionEnvironment;

    public WenyanVisitor(WenyanFunctionEnvironment functionEnvironment, WenyanControl control) {
        this.functionEnvironment = functionEnvironment;
        this.control = control;
    }

    public static WenyanRParser.ProgramContext program(String program) {
        WenyanRLexer lexer = new WenyanRLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new WenyanErrorListener());
        WenyanRParser parser = new WenyanRParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new WenyanErrorListener());
        return parser.program();
    }
}
