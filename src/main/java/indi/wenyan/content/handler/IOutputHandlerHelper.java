package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;

public interface IOutputHandlerHelper extends IExecCallHandler {

    void output(String message) throws WenyanException.WenyanThrowException;

    default IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        StringBuilder result = new StringBuilder();
        for (IWenyanValue arg : context.args()) {
            result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
        }
        output(result.toString());
        return WenyanNull.NULL;
    }
}
