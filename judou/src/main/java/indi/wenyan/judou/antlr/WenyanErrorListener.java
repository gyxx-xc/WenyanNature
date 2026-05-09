package indi.wenyan.judou.antlr;

import indi.wenyan.judou.api.WenyanCompileException;
import indi.wenyan.judou.api.language.JudouExceptionText;
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
        throw new WenyanCompileException(JudouExceptionText.LineError.string(line, charPositionInLine, msg));
    }
}
