package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanDataParser;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.judou.utils.language.JudouTypeText;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

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
    public static final WenyanType<WenyanBuiltinObjectType> TYPE = new WenyanType<>(JudouTypeText.DictObjectType.string(), WenyanBuiltinObjectType.class);

    public WenyanBuiltinObjectType(WenyanBuiltinObjectType parent) {
        this.parent = parent;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunctionHelper(name);
        if (attr != null) return attr;
        else
            throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
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

    public IWenyanValue getFunction(String id) throws WenyanException {
        var attr = getFunctionHelper(id);
        if (attr == null) {
            throw new WenyanException(JudouExceptionText.NoAttribute.string(id));
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
    public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException {
        throw new WenyanUnreachedException();
    }

    @Override
    public void call(IWenyanValue self, @UnknownNullability IWenyanRunner thread,
                     List<IWenyanValue> argsList) throws WenyanException {
        // create empty, run constructor, return self
        IWenyanValue selfObj = new WenyanBuiltinObject(this);
        thread.getCurrentRuntime().pushReturnValue(selfObj);

        WenyanBuiltinFunction constructor = getAttribute(WenyanDataParser.CONSTRUCTOR_ID)
                .as(WenyanBuiltinFunction.TYPE);

        WenyanFrame newRuntime = constructor.getNewRuntime(self, argsList, thread.getCurrentRuntime());
        newRuntime.setReturnBehavior((runner, ignore) -> runner.ret());
        thread.call(newRuntime);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
