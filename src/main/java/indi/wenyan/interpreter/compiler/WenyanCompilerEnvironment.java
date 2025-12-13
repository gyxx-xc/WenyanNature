package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.executor.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.HashMap;
import java.util.Stack;

/**
 * Environment for the Wenyan compiler that manages bytecode generation,
 * constant tables, identifier tables, and control flow structures.
 */
public class WenyanCompilerEnvironment {
    public static final int FUNCTION_ARGS_MAX = 100;
    private final WenyanBytecode bytecode;
    private final HashMap<Constant, Integer> constTable = new HashMap<>();
    private final HashMap<String, Integer> identifierTable = new HashMap<>();
    private final Stack<ForEnvironment> forStack = new Stack<>();
    private final Stack<Context> debugContextStack = new Stack<>();
    private int lastContextStart = 0;

    public boolean functionAttrFlag = false;

    /**
     * Represents a for-loop environment with its end labels.
     */
    private record ForEnvironment(int forEndLabel, int progEndLabel) {}

    /**
     * Represents a constant value with its type and object.
     */
    private record Constant(WenyanType<?> t, Object o) {}

    /**
     * Represents a debug context with source location information.
     */
    private record Context(int line, int column, int contentStart, int contentEnd) {}

    /**
     * Creates a new compiler environment with the specified bytecode.
     * @param bytecode The bytecode to use
     */
    public WenyanCompilerEnvironment(WenyanBytecode bytecode) {
        this.bytecode = bytecode;
    }

    /**
     * Adds a bytecode instruction with a value argument.
     * @param code The operation code
     * @param value The value argument
     */
    public void add(WenyanCode code, IWenyanValue value) {
        int index = getConstIndex(value);
        bytecode.add(code, index);
    }

    /**
     * Adds a bytecode instruction with an identifier argument.
     * @param code The operation code
     * @param identifier The identifier argument
     */
    public void add(WenyanCode code, String identifier) {
        int index = getIdentifierIndex(identifier);
        bytecode.add(code, index);
    }

    /**
     * Adds a bytecode instruction with an integer argument.
     * @param code The operation code
     * @param arg The integer argument
     */
    public void add(WenyanCode code, int arg) {
        bytecode.add(code, arg);
    }

    /**
     * Adds a bytecode instruction with no argument.
     * @param code The operation code
     */
    public void add(WenyanCode code) {
        bytecode.add(code, 0);
    }

    /**
     * Creates a new label in the bytecode.
     * @return The index of the new label
     */
    public int getNewLabel() {
        return bytecode.getNewLabel();
    }

    /**
     * Sets the current bytecode position as the target for a label.
     * @param label The label index
     */
    public void setLabel(int label) {
        bytecode.setLabel(label, bytecode.size());
    }

    /**
     * Enters a new for-loop context.
     */
    public void enterFor() {
        int forEndLabel = bytecode.getNewLabel();
        int progEndLabel = bytecode.getNewLabel();
        forStack.push(new ForEnvironment(forEndLabel, progEndLabel));
    }

    /**
     * Gets the label for the current for-loop end.
     * @return The for-loop end label
     */
    public int getForEndLabel() {
        return forStack.peek().forEndLabel;
    }

    /**
     * Gets the label for the current program end.
     * @return The program end label
     */
    public int getProgEndLabel() {
        return forStack.peek().progEndLabel;
    }

    /**
     * Sets the current bytecode position as the target for the program end label.
     */
    public void setProgEndLabel() {
        bytecode.setLabel(forStack.peek().progEndLabel, bytecode.size());
    }

    /**
     * Sets the current bytecode position as the target for the for-loop end label.
     */
    public void setForEndLabel() {
        bytecode.setLabel(forStack.peek().forEndLabel, bytecode.size());
    }

    /**
     * Exits the current for-loop context.
     */
    public void exitFor() {
        forStack.pop();
    }

    /**
     * Gets the constant index for a value, adding it if not present.
     * @param value The value to look up
     * @return The index of the constant
     */
    private int getConstIndex(IWenyanValue value) {
        Constant constant = new Constant(value.type(), value);
        Integer index = constTable.get(constant);
        if (index == null) {
            index = bytecode.addConst(value);
            constTable.put(constant, index);
        }
        return index;
    }

    /**
     * Gets the identifier index for a string, adding it if not present.
     * @param identifier The identifier to look up
     * @return The index of the identifier
     */
    public int getIdentifierIndex(String identifier) {
        Integer index = identifierTable.get(identifier);
        if (index == null) {
            index = bytecode.addIdentifier(identifier);
            identifierTable.put(identifier, index);
        }
        return index;
    }

    /**
     * Enters a new debug context.
     * @param line Line number
     * @param column Column number
     * @param contentStart Start index
     * @param contentEnd End index
     */
    public void enterContext(int line, int column, int contentStart, int contentEnd) {
        if (!debugContextStack.isEmpty()) {
            Context curContext = debugContextStack.peek();
            if (bytecode.size() != lastContextStart)
                bytecode.addContext(curContext.line, curContext.column, lastContextStart,
                        bytecode.size(), curContext.contentStart, curContext.contentEnd);
        }
        lastContextStart = bytecode.size();
        debugContextStack.push(new Context(line, column, contentStart, contentEnd));
    }

    /**
     * Exits the current debug context.
     */
    public void exitContext() {
        if (!debugContextStack.isEmpty()) {
            Context curContext = debugContextStack.pop();
            if (bytecode.size() != lastContextStart)
                bytecode.addContext(curContext.line, curContext.column, lastContextStart,
                        bytecode.size(), curContext.contentStart, curContext.contentEnd);
            lastContextStart = bytecode.size();
        }
    }
}
