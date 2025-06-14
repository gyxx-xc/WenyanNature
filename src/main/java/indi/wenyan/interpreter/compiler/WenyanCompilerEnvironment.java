package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.executor.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;

public class WenyanCompilerEnvironment implements Serializable {
    private final WenyanBytecode bytecode;
    private final HashMap<Constant, Integer> constTable = new HashMap<>();
    private final HashMap<String, Integer> identifierTable = new HashMap<>();
    private final Stack<ForEnvironment> forStack = new Stack<>();
    private final Stack<Context> debugContextStack = new Stack<>();
    private int lastContextStart = 0;

    public boolean functionAttrFlag = false;

    private record ForEnvironment(int forEndLabel, int progEndLabel) {}

    private record Constant(WenyanType t, Object o) {}
    private record Context(int line, int column) {}

    public WenyanCompilerEnvironment(WenyanBytecode bytecode) {
        this.bytecode = bytecode;
    }

    public void add(WenyanCode code, WenyanNativeValue value) {
        int index = getConstIndex(value);
        bytecode.add(code, index);
    }

    public void add(WenyanCode code, String identifier) {
        int index = getIdentifierIndex(identifier);
        bytecode.add(code, index);
    }

    public void add(WenyanCode code, int arg) {
        bytecode.add(code, arg);
    }

    public void add(WenyanCode code) {
        bytecode.add(code, 0);
    }

    public int getNewLabel() {
        return bytecode.getNewLabel();
    }

    public void setLabel(int label) {
        bytecode.setLabel(label, bytecode.size());
    }

    public void enterFor() {
        int forEndLabel = bytecode.getNewLabel();
        int progEndLabel = bytecode.getNewLabel();
        forStack.push(new ForEnvironment(forEndLabel, progEndLabel));
    }

    public int getForEndLabel() {
        return forStack.peek().forEndLabel;
    }
    public int getProgEndLabel() {
        return forStack.peek().progEndLabel;
    }
    public void setProgEndLabel() {
        bytecode.setLabel(forStack.peek().progEndLabel, bytecode.size());
    }
    public void setForEndLabel() {
        bytecode.setLabel(forStack.peek().forEndLabel, bytecode.size());
    }

    public void exitFor() {
        forStack.pop();
    }

    private int getConstIndex(WenyanNativeValue value) {
        Constant constant = new Constant(value.type(), value.getValue());
        Integer index = constTable.get(constant);
        if (index == null) {
            index = bytecode.addConst(value);
            constTable.put(constant, index);
        }
        return index;
    }

    public int getIdentifierIndex(String identifier) {
        Integer index = identifierTable.get(identifier);
        if (index == null) {
            index = bytecode.addIdentifier(identifier);
            identifierTable.put(identifier, index);
        }
        return index;
    }

    public void enterContext(int line, int column) {
        if (!debugContextStack.isEmpty()) {
            Context curContext = debugContextStack.peek();
            if (bytecode.size() != lastContextStart)
                bytecode.addContext(curContext.line, curContext.column, lastContextStart, bytecode.size());
        }
        lastContextStart = bytecode.size();
        debugContextStack.push(new Context(line, column));
    }

    public void exitContext() {
        if (!debugContextStack.isEmpty()) {
            Context curContext = debugContextStack.pop();
            if (bytecode.size() != lastContextStart)
                bytecode.addContext(curContext.line, curContext.column, lastContextStart, bytecode.size());
            lastContextStart = bytecode.size();
        }
    }
}
