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

public abstract class WenyanVisitor extends WenyanRBaseVisitor<Boolean> {
    protected final WenyanCompilerEnvironment bytecode;

    public WenyanVisitor(WenyanCompilerEnvironment bytecode) {
        this.bytecode = bytecode;
    }

    @Override
    public Boolean visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx) {
            bytecode.enterContext(ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }
        Boolean result = super.visit(tree);
        if (tree instanceof ParserRuleContext) {
            bytecode.exitContext();
        }
        return result;
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
