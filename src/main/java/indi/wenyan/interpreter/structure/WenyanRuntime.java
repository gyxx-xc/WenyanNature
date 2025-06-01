package indi.wenyan.interpreter.structure;

import java.util.HashMap;
import java.util.Stack;

public class WenyanRuntime {
    public final WenyanBytecode bytecode;

    public final HashMap<String, WenyanValue> variables = new HashMap<>();
    public final WenyanStack resultStack = new WenyanStack();
    public final Stack<WenyanValue> processStack = new Stack<>();

    public int programCounter = 0;
    public boolean PCFlag = false;
    public boolean noReturnFlag = false;

    public WenyanRuntime(WenyanBytecode bytecode) {
        this.bytecode = bytecode;
    }

    public void setVariable(String id, WenyanValue value) {
        variables.put(id, value);
    }

    public void importEnvironment(WenyanRuntime environment) {
        variables.putAll(environment.variables);
    }
}
