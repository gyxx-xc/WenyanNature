package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanCode;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanRuntime runtime) {
        switch (operation) {
            case CALL -> {
                // setup new Runtime
                WenyanProgramCode code;
                try {
                    code = ((WenyanValue.FunctionSign)runtime.processStack.pop().casting(WenyanValue.Type.FUNCTION).getValue()).bytecode();
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
                if (code instanceof JavacallHandler) {
                    try {
                        WenyanValue[] argsList = new WenyanValue[args];
                        for (int i = 0; i < args; i++)
                            argsList[i] = runtime.processStack.pop();
                        ((JavacallHandler) code).handle(argsList);
                        return;
                    } catch (WenyanException.WenyanThrowException e) {
                        throw new WenyanException(e.getMessage());
                    }
                }
                WenyanRuntime newRuntime = new WenyanRuntime(runtime, (WenyanBytecode) code);
                // change control
            }
            case RETURN -> {
                WenyanValue returnValue = runtime.processStack.pop();
                // TODO: change control

                runtime.processStack.push(returnValue);
            }
        }
    }

    public enum Operation {
        CALL,
        RETURN
    }

    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case RETURN -> "RETURN";
        };
    }

}
