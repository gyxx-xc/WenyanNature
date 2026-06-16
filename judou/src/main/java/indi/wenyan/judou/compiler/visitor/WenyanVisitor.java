package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanErrorListener;
import indi.wenyan.judou.antlr.WenyanLexer;
import indi.wenyan.judou.antlr.WenyanParser;
import indi.wenyan.judou.antlr.WenyanParserBaseVisitor;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 * Base visitor for Wenyan language that provides common functionality.
 * Handles context tracking and provides utilities for parsing Wenyan code.
 */
public abstract class WenyanVisitor extends WenyanParserBaseVisitor<Boolean> {
    /**
     * The compiler environment used to emit bytecode
     */
    protected final WenyanCompilerEnvironment bytecode;

    /**
     * Constructs a visitor with the given bytecode environment
     *
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
            bytecode.enterContext(ctx);
            if (bytecode.isDebug() && ctx instanceof WenyanParser.StatementContext) {
                bytecode.add(WenyanCodes.BREAKPOINT);
            }
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
        for (int i = 0; i < n; i++) {
            if (!shouldVisitNextChild(node, result)) {
                break;
            }

            ParseTree c = node.getChild(i);
            Boolean childResult;
            if (c instanceof ParserRuleContext ctx) {
                bytecode.enterContext(ctx);
                if (bytecode.isDebug() && ctx instanceof WenyanParser.StatementContext) {
                    bytecode.add(WenyanCodes.BREAKPOINT);
                }
                childResult = c.accept(this);
                bytecode.exitContext();
            } else {
                childResult = c.accept(this);
            }
            result = aggregateResult(result, childResult);
        }

        return result;
    }

    public static void generateTo(String code, WenyanCompilerEnvironment environment) {
        WenyanLexer lexer = new WenyanLexer(
                CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new WenyanErrorListener());

        WenyanParser parser = new WenyanParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new WenyanErrorListener());

        new WenyanMainVisitor(environment).visit(parser.program());
    }
}
