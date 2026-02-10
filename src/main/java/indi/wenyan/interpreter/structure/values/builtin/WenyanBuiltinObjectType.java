package indi.wenyan.interpreter.structure.values.builtin;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * Built-in object type implementation for Wenyan language.
 * Represents an object type created in Wenyan code.
 */
public class WenyanBuiltinObjectType implements IWenyanObjectType {
    @Getter
    private final WenyanBuiltinObjectType parent;
    private final HashMap<String, IWenyanValue> staticVariable = new HashMap<>();
    private final HashMap<String, IWenyanValue> functions = new HashMap<>();
    public static final WenyanType<WenyanBuiltinObjectType> TYPE = new WenyanType<>("dict_object_type", WenyanBuiltinObjectType.class);

    public WenyanBuiltinObjectType(WenyanBuiltinObjectType parent) {
        this.parent = parent;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanThrowException {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunctionHelper(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_not_found_").getString() + name);
        else
            return attr;
    }

    @Nullable
    protected IWenyanValue getFunctionHelper(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunctionHelper(id);
        } else {
            return null;
        }
    }

    public IWenyanValue getFunction(String id) throws WenyanThrowException {
        var attr = getFunctionHelper(id);
        if (attr == null) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_not_found_").getString() + id);
        }
        return attr;
    }

    public void addFunction(String id, IWenyanValue function) {
        functions.put(id, function);
    }

    public IWenyanValue getStaticVariable(String id) {
        return staticVariable.get(id);
    }

    public void addStaticVariable(String id, IWenyanValue value) {
        staticVariable.put(id, value);
    }

    @Override
    public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanThrowException {
        throw new WenyanException.WenyanUnreachedException();
    }

    @Override
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList) throws WenyanThrowException {
        // create empty, run constructor, return self
        IWenyanValue selfObj = new WenyanBuiltinObject(this);
        thread.currentRuntime().pushReturnValue(selfObj);

        IWenyanFunction constructor = getAttribute(WenyanDataParser.CONSTRUCTOR_ID)
                .as(IWenyanFunction.TYPE);

        constructor.call(selfObj, thread, argsList); // we got a runtime change here
        thread.currentRuntime().noReturnFlag = true;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
