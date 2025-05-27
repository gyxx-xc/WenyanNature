package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Stack;

public class WenyanRuntime {
    private final WenyanRuntime parentEnvironment;
    private final WenyanBytecode bytecode;
    public int programCounter = 0;
    private final HashMap<String, WenyanValue> variables = new HashMap<>();
    public final WenyanStack resultStack = new WenyanStack();
    public final Stack<WenyanValue> processStack = new Stack<>();

    public WenyanRuntime(WenyanRuntime parentEnvironment, WenyanBytecode bytecode) {
        this.parentEnvironment = parentEnvironment;
        this.bytecode = bytecode;
    }

    public WenyanValue getVariable(int id) {
        return getVariableHelper(bytecode.getIdentifier(id));
    }

    public String getVariableId(int id) {
        return bytecode.getIdentifier(id);
    }

    public WenyanValue getConstant(int id) {
        return bytecode.getConst(id);
    }

    public int getLabel(int id) {
        return bytecode.getLabel(id);
    }

    public WenyanValue getVariableHelper(String id) {
        if (variables.containsKey(id)) {
            return variables.get(id);
        } else if (parentEnvironment != null) {
            return parentEnvironment.getVariableHelper(id);
        } else {
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString()+id);
        }
    }

    public void setVariable(String id, WenyanValue value) {
        variables.put(id, value);
    }

    public void importEnvironment(WenyanRuntime environment) {
        variables.putAll(environment.variables);
    }
}
