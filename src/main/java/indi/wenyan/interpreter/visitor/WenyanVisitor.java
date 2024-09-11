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
    protected Semaphore semaphore;
    protected WenyanFunctionEnvironment functionEnvironment;

    public WenyanVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore semaphore) {
        this.functionEnvironment = functionEnvironment;
        this.semaphore = semaphore;
    }

    public static Semaphore run(WenyanFunctionEnvironment functionEnvironment,
                                Thread.UncaughtExceptionHandler uncaughtExceptionHandler,
                                String program) {
        WenyanRLexer lexer = new WenyanRLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new WenyanErrorListener());
        WenyanRParser parser = new WenyanRParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new WenyanErrorListener());

        // ready to visit
        Semaphore semaphore = new Semaphore(1);
        Thread wenyanProgram = new Thread(() -> new WenyanMainVisitor(functionEnvironment, semaphore).visit(parser.program()));
        wenyanProgram.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        wenyanProgram.start();
        return semaphore;
    }
}
