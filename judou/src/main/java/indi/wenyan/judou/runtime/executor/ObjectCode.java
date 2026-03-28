package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.*;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObject;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObjectType;

/**
 * Handles object-related operations in the Wenyan interpreter.
 */
public enum ObjectCode {;

    static void loadAttr(int arg, IWenyanRunner thread, boolean isNotRemain) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        String id = runtime.getBytecode().getIdentifier(arg);
        IWenyanValue attr;
        IWenyanValue value = isNotRemain ?
                runtime.getProcessStack().pop() : runtime.getProcessStack().peek();
        assert value != null;
        if (value.is(IWenyanObjectType.TYPE)) {
            IWenyanObjectType object = value.as(IWenyanObjectType.TYPE);
            attr = object.getAttribute(id);
        } else {
            IWenyanObject object = value.as(IWenyanObject.TYPE);
            attr = object.getAttribute(id);
        }
        runtime.pushReturnValue(attr);
    }

    static void createType(IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        var parent = runtime.getProcessStack().pop();
        IWenyanValue type;
        if (parent.is(WenyanNull.TYPE))
            type = new WenyanBuiltinObjectType(null);
        else
            type = new WenyanBuiltinObjectType(parent
                    .as(WenyanBuiltinObjectType.TYPE));
        runtime.pushReturnValue(type);
    }

    static void storeAttr(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        String id = runtime.getBytecode().getIdentifier(arg);
        WenyanBuiltinObject self = runtime.getProcessStack().pop().as(WenyanBuiltinObject.TYPE);
        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
        self.createAttribute(id, value);
    }

    static void storeFunctionAttr(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        String id = runtime.getBytecode().getIdentifier(arg);
        IWenyanValue value = runtime.getProcessStack().pop();
        assert runtime.getProcessStack().peek() != null;
        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
        type.addFunction(id, value);
    }

    static void storeStaticAttr(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        String id = runtime.getBytecode().getIdentifier(arg);
        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
        assert runtime.getProcessStack().peek() != null;
        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
        type.addStaticVariable(id, value);
    }
}
