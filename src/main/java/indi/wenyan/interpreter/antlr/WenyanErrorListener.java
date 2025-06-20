package indi.wenyan.interpreter.antlr;

import indi.wenyan.interpreter.structure.WenyanException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class WenyanErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {
        throw new WenyanException("line " + line + ":" + charPositionInLine + " " + msg + "\nwith " + e);
    }
}
