package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavaCallCodeWarper;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanDataPhaser;
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
    public void exec(int args, WenyanThread thread) {
        try {
            WenyanRuntime runtime = thread.currentRuntime();
            WenyanValue func = runtime.processStack.pop();
            WenyanValue.FunctionSign sign = (WenyanValue.FunctionSign)
                    func.casting(WenyanValue.Type.FUNCTION).getValue();
            WenyanValue self = null;
            boolean noReturn;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.getType() == WenyanValue.Type.OBJECT_TYPE) {
                // create empty, run constructor, return self
                self = new WenyanValue(WenyanValue.Type.OBJECT,
                        new WenyanObject((WenyanObjectType)
                                func.casting(WenyanValue.Type.OBJECT_TYPE).getValue()), true);
                runtime.processStack.push(self);
                noReturn = true;
            } else { // function
                // handle self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try {
                        self = self.casting(WenyanValue.Type.OBJECT);
                    } catch (WenyanException.WenyanTypeException e) {
                        // ignore self then
                        self = null;
                    }
                }
                noReturn = false;
            }

            // must make the callF at end, because it may block thread
            // which is a fake block, it will still run the rest command before blocked
            // it will only block the next WenyanCode being executed
            callFunction(sign, self, thread, args, noReturn);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    private void callFunction
            (WenyanValue.FunctionSign sign, WenyanValue self,
             WenyanThread thread, int args, boolean noReturn)
            throws WenyanException.WenyanThrowException {

        WenyanRuntime runtime = thread.currentRuntime();
        if (sign.bytecode() instanceof JavaCallCodeWarper warper) {
            List<WenyanValue> argsList = new ArrayList<>(args);
            if (self != null)
                argsList.add(self);
            for (int i = 0; i < args; i++)
                argsList.add(runtime.processStack.pop());

            if (warper.handler.isLocal()) {
                warper.handler.handle(thread, argsList.toArray(new WenyanValue[0]), noReturn);
            } else {
                JavaCallCodeWarper.Request request = new JavaCallCodeWarper.Request(
                        thread, argsList.toArray(new WenyanValue[0]), noReturn, warper.handler);
                thread.program.requestThreads.add(request);
                thread.block();
            }
        } else {
            WenyanValue[] argsList = new WenyanValue[args];
            if (sign.argTypes().length != args)
                throw new WenyanException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            for (int i = 0; i < args; i++)
                argsList[i] = runtime.processStack.pop().casting(sign.argTypes()[i]);

            WenyanRuntime newRuntime = new WenyanRuntime((WenyanBytecode) sign.bytecode());
            if (self != null) {
                newRuntime.setVariable(WenyanDataPhaser.SELF_ID, self);
                newRuntime.setVariable(WenyanDataPhaser.PARENT_ID, new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                        ((WenyanObject) self.getValue()).type.parent, true));
            }
            // STUB: assume the first n id is the args
            for (int i = 0; i < args; i++)
                newRuntime.setVariable(
                        ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                        WenyanValue.varOf(argsList[i]));
            newRuntime.noReturnFlag = noReturn;
            thread.add(newRuntime);
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) {
        WenyanValue.FunctionSign sign;
        try {
            sign = (WenyanValue.FunctionSign)
                    thread.currentRuntime().processStack.peek()
                            .casting(WenyanValue.Type.FUNCTION).getValue();
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (sign.bytecode() instanceof JavaCallCodeWarper warper) {
            return warper.handler.getStep(args, thread);
        } else {
            return args;
        }
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
