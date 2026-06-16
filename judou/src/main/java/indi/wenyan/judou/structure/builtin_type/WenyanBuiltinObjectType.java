package indi.wenyan.judou.structure.builtin_type;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.exec.ICrossFunctionExecutable;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.language.Symbol;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanObjectType;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Built-in object type implementation for Wenyan language.
 * Represents an object type created in Wenyan code.
 */
public final class WenyanBuiltinObjectType implements IWenyanObjectType, ICrossFunctionExecutable {
    public static final WenyanType<WenyanBuiltinObjectType> TYPE = new WenyanType<>(JudouTypeText.DictObjectType.string(), WenyanBuiltinObjectType.class);

    @Getter
    private final WenyanBuiltinObjectType parent;
    private final HashMap<String, IWenyanValue> staticVariable = new HashMap<>();
    private final HashMap<String, IWenyanValue> functions = new HashMap<>();

    public WenyanBuiltinObjectType(WenyanBuiltinObjectType parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull IWenyanValue getAttribute(String name) throws WenyanException {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunctionHelper(name);
        if (attr != null) return attr;
        else
            throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
    }

    @Nullable
    private IWenyanValue getFunctionHelper(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunctionHelper(id);
        } else {
            return null;
        }
    }

    public @NotNull IWenyanValue getFunction(String id) throws WenyanException {
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
    public void callWithReturn(@Nullable IWenyanValue self, @NotNull IWenyanRunner thread, List<IWenyanValue> argsList,
                               Consumer<IWenyanValue> onReturn) throws WenyanException {
        // create empty, run constructor, return self
        IWenyanValue selfObj = new WenyanBuiltinObject(this);

        WenyanBuiltinFunction constructor = getAttribute(Symbol.CONSTRUCTOR_ID)
                .as(WenyanBuiltinFunction.TYPE);

        WenyanFrame newRuntime = constructor.getNewRuntime(self, argsList, thread.getCurrentRuntime(),
                (runner, ignore) -> {
                    runner.getFrameManager().ret();
                    onReturn.accept(selfObj);
                });
        thread.getFrameManager().call(newRuntime);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
