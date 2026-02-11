package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.WenyanDataParser;
import lombok.Getter;
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
            throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.function_not_found_") + name);
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
            throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.function_not_found_") + id);
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
