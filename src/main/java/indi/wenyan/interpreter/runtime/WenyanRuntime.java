package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;

import java.util.HashMap;
import java.util.Stack;

public class WenyanRuntime {
    public final WenyanBytecode bytecode;

    public final HashMap<String, IWenyanValue> variables = new HashMap<>();
    public final WenyanStack resultStack = new WenyanStack();
    public final Stack<IWenyanValue> processStack = new Stack<>();

    public int programCounter = 0;
    public boolean PCFlag = false;
    public boolean noReturnFlag = false;

    public WenyanRuntime(WenyanBytecode bytecode) {
        this.bytecode = bytecode;
    }

    public void setVariable(String id, IWenyanValue value) {
        variables.put(id, value);
    }

    public void importEnvironment(WenyanPackage environment) {
        variables.putAll(environment.getVariables());
    }
}
