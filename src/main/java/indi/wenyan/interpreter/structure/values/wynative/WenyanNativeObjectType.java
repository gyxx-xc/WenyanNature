package indi.wenyan.interpreter.structure.values.wynative;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;

public class WenyanNativeObjectType implements IWenyanObjectType {
    private final WenyanNativeObjectType parent;
    private final HashMap<String, IWenyanValue> staticVariable = new HashMap<>();
    private final HashMap<String, IWenyanValue> functions = new HashMap<>();
    public static final WenyanType<WenyanNativeObjectType> TYPE = new WenyanType<>("dict_object_type", WenyanNativeObjectType.class);

    public WenyanNativeObjectType(WenyanNativeObjectType parent) {
        this.parent = parent;
    }

    public WenyanNativeObjectType getParent() {
        return parent;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunction(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_not_found_").getString() + name);
        else
            return attr;
    }

    public IWenyanValue getFunction(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunction(id);
        } else {
            return null;
        }
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
    public IWenyanObject createObject(List<IWenyanValue> argsList) {
        return null;
    }

    @Override
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        // create empty, run constructor, return self
        IWenyanValue selfObj = new WenyanNativeObject(this);
        thread.currentRuntime().processStack.push(selfObj);

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
