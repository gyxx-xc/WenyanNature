package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanCodes;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanRuntime runtime) {
        if (operation == Operation.RETURN) {
            if (!runtime.noReturnFlag)
                runtime.parentEnvironment.processStack.push(runtime.processStack.pop());
            runtime.nextRuntime = runtime.parentEnvironment;
            runtime.changeRuntimeFlag = true;
            return;
        }

        if (runtime.processStack.peek().getType() == WenyanValue.Type.OBJECT_TYPE) {
            if (operation == Operation.CALL_ATTR) {
                WenyanValue value = runtime.processStack.pop();
                runtime.processStack.pop();
                runtime.processStack.push(value);
            }
            WenyanCodes.CREATE_OBJECT.exec(args, runtime);
            return;
        }

        // setup new Runtime
        boolean isInstanceCall = false;
        WenyanValue.FunctionSign sign;
        WenyanValue[] argsList = new WenyanValue[args];

        try {
            sign = ((WenyanValue.FunctionSign)runtime.processStack.pop().casting(WenyanValue.Type.FUNCTION).getValue());
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (operation == Operation.CALL_ATTR) {
            // depend on the types to choose call with self or not
            if (runtime.processStack.peek().getType() != WenyanValue.Type.OBJECT) {
                runtime.processStack.pop();
            } else {
                args += 1;
                isInstanceCall = true;
            }
        }
        for (int i = 0; i < args; i++)
            argsList[i] = runtime.processStack.pop();

        if (sign.bytecode() instanceof JavacallHandler) {
            try {
                WenyanValue value = ((JavacallHandler) sign.bytecode()).handle(argsList);
                runtime.processStack.push(value);
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage());
            }
        } else {
            runtime.nextRuntime = new WenyanRuntime(runtime, (WenyanBytecode) sign.bytecode());
            if (isInstanceCall) runtime.nextRuntime.setVariable("己", argsList[0]);
            for (int i = 1; i < args; i ++)
                runtime.nextRuntime.setVariable(
                        ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                        WenyanValue.varOf(argsList[i]));
            runtime.changeRuntimeFlag = true;
        }
    }


    public enum Operation {
        CALL,
        RETURN,
        CALL_ATTR
    }

    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case RETURN -> "RETURN";
            case CALL_ATTR -> "CALL_ATTR";
        };
    }

}
