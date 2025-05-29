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
                WenyanValue.FunctionSign sign;
                WenyanValue[] argsList = new WenyanValue[args];

                try {
                    sign = ((WenyanValue.FunctionSign)runtime.processStack.pop().casting(WenyanValue.Type.FUNCTION).getValue());
                    for (int i = 0; i < args; i++)
                        argsList[i] = runtime.processStack.pop();
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }


                if (sign.bytecode() instanceof JavacallHandler) {
                    try {
                        WenyanValue value = ((JavacallHandler) sign.bytecode()).handle(argsList);
                        runtime.processStack.push(value);
                    } catch (WenyanException.WenyanThrowException e) {
                        throw new WenyanException(e.getMessage());
                    }
                } else {
                    runtime.nextRuntime = new WenyanRuntime(runtime, (WenyanBytecode) sign.bytecode());
                    for (int i = 0; i < args; i ++) {
                        runtime.nextRuntime.setVariable(((WenyanBytecode) sign.bytecode()).getIdentifier(i), WenyanValue.varOf(argsList[i]));
                    }
                    runtime.changeRuntimeFlag = true;
                }
            }
            case RETURN -> {
                runtime.parentEnvironment.processStack.push(runtime.processStack.pop());
                runtime.nextRuntime = runtime.parentEnvironment;
                runtime.changeRuntimeFlag = true;
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
