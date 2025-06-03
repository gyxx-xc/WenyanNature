package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavaCallCodeWarper;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanProgram;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    // func / create_obj
    // -> ori_func / javacall
    @Override
    public void exec(int args, WenyanProgram program) {
        try {
            WenyanRuntime runtime = program.curThreads.cur();
            WenyanValue func = runtime.processStack.pop();
            WenyanValue self = null;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.getType() == WenyanValue.Type.OBJECT_TYPE) {
                // create empty, run constructor, return self
                self = new WenyanValue(WenyanValue.Type.OBJECT,
                        new WenyanObject((WenyanObjectType)
                                func.casting(WenyanValue.Type.OBJECT_TYPE).getValue()), true);
                WenyanValue.FunctionSign sign = (WenyanValue.FunctionSign)
                        func.casting(WenyanValue.Type.FUNCTION).getValue();

                callFunction(sign, self, program, args, true);
                runtime.processStack.push(self);
            } else { // function
                // handle self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try { self = self.casting(WenyanValue.Type.OBJECT);
                    } catch (WenyanException.WenyanTypeException ignore) {}

                    // ignore
                    if (self.getType() != WenyanValue.Type.OBJECT)
                        self = null;
                }

                WenyanValue.FunctionSign sign = ((WenyanValue.FunctionSign)
                        func.casting(WenyanValue.Type.FUNCTION).getValue());
                callFunction(sign, self, program, args, false);
            }
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    private void callFunction
            (WenyanValue.FunctionSign sign, WenyanValue self,
             WenyanProgram program, int args, boolean noReturn)
            throws WenyanException.WenyanThrowException {

        WenyanRuntime runtime = program.curThreads.cur();
        if (sign.bytecode() instanceof JavaCallCodeWarper warper) {
            List<WenyanValue> argsList = new ArrayList<>(args);
            if (self != null)
                argsList.add(self);
            for (int i = 0; i < args; i++)
                argsList.add(runtime.processStack.pop());

            WenyanValue value = warper.handler.handle(argsList.toArray(new WenyanValue[0]));
            if (!noReturn)
                runtime.processStack.push(value);
        } else {
            WenyanValue[] argsList = new WenyanValue[args];
            if (sign.argTypes().length != args)
                throw new WenyanException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            for (int i = 0; i < args; i++)
                argsList[i] = runtime.processStack.pop().casting(sign.argTypes()[i]);

            WenyanRuntime newRuntime = new WenyanRuntime((WenyanBytecode) sign.bytecode());
            if (self != null) {
                newRuntime.setVariable("己", self);
                newRuntime.setVariable("父", new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                        ((WenyanObject) self.getValue()).type.parent, true));
            }
            // STUB: assume the first n id is the args
            for (int i = 0; i < args; i++)
                newRuntime.setVariable(
                        ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                        WenyanValue.varOf(argsList[i]));
            newRuntime.noReturnFlag = noReturn;
            program.curThreads.add(newRuntime);
        }
    }

    @Override
    public int getStep(int args, WenyanProgram program) {
        return super.getStep(args, program);
    }

    public enum Operation {
        CALL,
        CALL_ATTR
    }

    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case CALL_ATTR -> "CALL_ATTR";
        };
    }
}
