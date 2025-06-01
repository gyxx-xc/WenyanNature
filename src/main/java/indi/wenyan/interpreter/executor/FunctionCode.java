package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavaCallCodeWarper;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanProgram;
import net.minecraft.network.chat.Component;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.runtimes.peek();
        if (operation == Operation.RETURN) {
            program.runtimes.pop();
            if (!runtime.noReturnFlag)
                program.runtimes.peek().processStack.push(runtime.processStack.pop());
            return;
        }

        if (runtime.processStack.peek().getType() == WenyanValue.Type.OBJECT_TYPE) {
            if (operation == Operation.CALL_ATTR) {
                WenyanValue value = runtime.processStack.pop();
                runtime.processStack.pop();
                runtime.processStack.push(value);
            }

            // override the call to create object
            runtime.bytecode.setTemp(runtime.programCounter, WenyanCodes.CREATE_OBJECT, args);
            runtime.PCFlag = true;
            return;
        }

        // setup new Runtime
        WenyanValue.FunctionSign sign;
        WenyanValue self = null; // for call attr

        try {
            sign = ((WenyanValue.FunctionSign)runtime.processStack.pop()
                    .casting(WenyanValue.Type.FUNCTION).getValue());
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (operation == Operation.CALL_ATTR) {
            // depend on the types to choose call with self or not
            if (runtime.processStack.peek().getType() != WenyanValue.Type.OBJECT) {
                runtime.processStack.pop();
            } else {
                self = runtime.processStack.pop();
            }
        }

        WenyanValue[] argsList = new WenyanValue[args];
        if (sign.bytecode() instanceof JavaCallCodeWarper warper) {
            runtime.bytecode.setTemp(runtime.programCounter, warper.handler, args);
            runtime.PCFlag = true;
            return;
        }

        if (sign.argTypes().length != args)
            throw new WenyanException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        try {
            for (int i = 0; i < args; i++)
                argsList[i] = runtime.processStack.pop().casting(sign.argTypes()[i]);
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }

        program.runtimes.push(new WenyanRuntime((WenyanBytecode) sign.bytecode()));
        runtime = program.runtimes.peek();
        if (self != null) {
            runtime.setVariable("己", self);
            runtime.setVariable("父", new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                    ((WenyanObject) self.getValue()).type.parent, true));
        }
        // STUB: assume the first n id is the args
        for (int i = 0; i < args; i ++)
            runtime.setVariable(
                    ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                    WenyanValue.varOf(argsList[i]));
    }

    @Override
    public int getStep(int args, WenyanProgram program) {
        if (operation != Operation.RETURN) {
            return args;
        }
        return super.getStep(args, program);
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
