package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanErrorListener;
import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanParseTreeException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

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

    // HACK: enter/exit context for every node
    @Override
    public Boolean visit(ParseTree tree) {
        Boolean result;
        if (tree instanceof ParserRuleContext ctx) {
            bytecode.enterContext(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                    ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex() + 1);
            result = super.visit(tree);
            bytecode.exitContext();
        } else {
            result = super.visit(tree);
        }
        return result;
    }

    @Override
    public Boolean visitChildren(RuleNode node) {
        Boolean result = defaultResult();
        int n = node.getChildCount();
        for (int i=0; i<n; i++) {
            if (!shouldVisitNextChild(node, result)) {
                break;
            }

            ParseTree c = node.getChild(i);
            Boolean childResult;
            if (c instanceof ParserRuleContext ctx) {
                // STUB: not sure why getStop will return null is empty (should be start)
                if (ctx.getStop() == null) throw new WenyanParseTreeException("content empty");
                bytecode.enterContext(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine(),
                        ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex() + 1);
                childResult = c.accept(this);
                bytecode.exitContext();
            } else {
                childResult = c.accept(this);
            }
            result = aggregateResult(result, childResult);
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
