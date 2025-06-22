package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;

public class WenyanDictObjectType implements WenyanObjectType {
    private final WenyanDictObjectType parent;
    private final HashMap<String, WenyanValue> staticVariable = new HashMap<>();
    private final HashMap<String, WenyanValue> functions = new HashMap<>();

    public WenyanDictObjectType(WenyanDictObjectType parent) {
        this.parent = parent;
    }

    public WenyanDictObjectType getParent() {
        return parent;
    }

    @Override
    public WenyanValue getAttribute(String name) {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunction(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_").getString() + name);
        else
            return attr;
    }

    public WenyanValue getFunction(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunction(id);
        } else {
            return null;
        }
    }

    public void addFunction(String id, WenyanValue function) {
        functions.put(id, function);
    }

    public WenyanValue getStaticVariable(String id) {
        return staticVariable.get(id);
    }

    public void addStaticVariable(String id, WenyanValue value) {
        staticVariable.put(id, value);
    }

    @Override
    public WenyanObject createObject(List<WenyanValue> argsList) {
        return null;
    }

    @Override
    public void call(WenyanValue self, WenyanThread thread,
                     List<WenyanValue> argsList) throws WenyanException.WenyanThrowException {
        // create empty, run constructor, return self
        self = new WenyanNativeValue1(WenyanObject.TYPE,
                new WenyanDictObject(this), true);
        thread.currentRuntime().processStack.push(self);

        WenyanFunction constructor = (WenyanFunction) getAttribute(WenyanDataParser.CONSTRUCTOR_ID)
                .casting(WenyanFunction.TYPE).getValue();

        constructor.call(self, thread, argsList); // we got a runtime change here
        thread.currentRuntime().noReturnFlag = true;
    }
}
