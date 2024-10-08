package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanErrorListener;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.concurrent.Semaphore;

public abstract class WenyanVisitor extends WenyanRBaseVisitor<WenyanValue> {
    protected final Semaphore entitySemaphore;
    protected final Semaphore programSemaphore;
    protected final WenyanFunctionEnvironment functionEnvironment;

    public WenyanVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore programSemaphore, Semaphore entitySemaphore) {
        this.functionEnvironment = functionEnvironment;
        this.entitySemaphore = entitySemaphore;
        this.programSemaphore = programSemaphore;
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
