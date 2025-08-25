package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanErrorListener;
import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Base visitor for Wenyan language that provides common functionality.
 * Handles context tracking and provides utilities for parsing Wenyan code.
 */
public abstract class WenyanVisitor extends WenyanRBaseVisitor<Boolean> {
    /**
     * The compiler environment used to emit bytecode
     */
    protected final WenyanCompilerEnvironment bytecode;

    /**
     * Constructs a visitor with the given bytecode environment
     * @param bytecode The compiler environment to emit bytecode to
     */
    protected WenyanVisitor(WenyanCompilerEnvironment bytecode) {
        this.bytecode = bytecode;
    }

    @Override
    public Boolean visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx) {
            bytecode.enterContext(ctx.start.getLine(), ctx.start.getCharPositionInLine(), ctx.getText());
        }
        Boolean result = super.visit(tree);
        if (tree instanceof ParserRuleContext) {
            bytecode.exitContext();
        }
        return result;
    }

    /**
     * Parses a Wenyan program string into an AST
     * @param program The Wenyan program as a string
     * @return The parsed program context
     */
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
