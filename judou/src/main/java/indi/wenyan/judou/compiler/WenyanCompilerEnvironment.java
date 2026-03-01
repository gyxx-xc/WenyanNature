package indi.wenyan.judou.compiler;

import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Environment for the Wenyan compiler that manages bytecode generation,
 * constant tables, identifier tables, and control flow structures.
 */
public class WenyanCompilerEnvironment {
    public static final int FUNCTION_ARGS_MAX = 100;
    private final WenyanBytecode bytecode;
    @Nullable
    private final WenyanCompilerEnvironment parent;
    private final HashMap<IWenyanValue, Integer> constTable = new HashMap<>();
    private final HashMap<String, Integer> identifierTable = new HashMap<>();
    private final HashMap<ScopedValue, Integer> capturedValueTable = new HashMap<>();
    private final Deque<ForEnvironment> forStack = new ArrayDeque<>();
    private final Deque<Context> debugContextStack = new ArrayDeque<>();
    private final Deque<Scope> scopeStack = new ArrayDeque<>();
    private int lastContextStart = 0;
    private int localVariableCounter = 0;

    /**
     * Represents a for-loop environment with its end labels.
     */
    private record ForEnvironment(int forEndLabel, int progEndLabel) {
    }

    /**
     * Represents a debug context with source location information.
     */
    private record Context(int line, int column, int contentStart, int contentEnd) {
    }

    /**
     * Creates a new compiler environment with the specified bytecode.
     *
     * @param bytecode The bytecode to use
     */
    public WenyanCompilerEnvironment(WenyanBytecode bytecode, @Nullable WenyanCompilerEnvironment parent, List<String> argv) {
        this.parent = parent;
        this.bytecode = bytecode;
        var scope = new Scope(0);
        scopeStack.push(scope);
        for (String arg : argv) {
            if (scope.varibles.containsKey(arg))
                throw new WenyanCompileException("變量名稱重複");
            scope.varibles.put(arg, localVariableCounter ++);
        }
    }

    /**
     * Adds a bytecode instruction with a value argument.
     *
     * @param code  The operation code
     * @param value The value argument
     */
    public void add(WenyanCodes code, IWenyanValue value) {
        int index = getConstIndex(value);
        add(code, index);
    }

    /**
     * Adds a bytecode instruction with an identifier argument.
     *
     * @param code       The operation code
     * @param identifier The identifier argument
     */
    public void add(WenyanCodes code, String identifier) {
        int index = getIdentifierIndex(identifier);
        add(code, index);
    }

    public void addStoreCode(String identifier) {
        Scope currentScope = scopeStack.peek();
        assert currentScope != null;
        if (currentScope.varibles.containsKey(identifier)) {
            int index = currentScope.varibles.get(identifier);
            add(WenyanCodes.STORE, index);
        }
        int newIndex = localVariableCounter ++;
        currentScope.varibles.put(identifier, newIndex);
        add(WenyanCodes.STORE, newIndex);
    }

    public void addLoadCode(String identifier) {
        var scopedVariable = getScopedValue(identifier);
        if (scopedVariable == null) {
            add(WenyanCodes.LOAD_GLOBAL, identifier);
        } else {
            if (scopedVariable.capturedValue.fromLocal()) {
                add(WenyanCodes.LOAD, scopedVariable.capturedValue.index());
            } else {
                add(WenyanCodes.LOAD_REF, scopedVariable.capturedValue.index());
            }
        }
    }

    /**
     * Adds a bytecode instruction with an integer argument.
     *
     * @param code The operation code
     * @param arg  The integer argument
     */
    public void add(WenyanCodes code, int arg) {
        bytecode.add(code, arg);
    }

    /**
     * Adds a bytecode instruction with no argument.
     *
     * @param code The operation code
     */
    public void add(WenyanCodes code) {
        bytecode.add(code, 0);
    }

    /**
     * Creates a new label in the bytecode.
     *
     * @return The index of the new label
     */
    public int getNewLabel() {
        return bytecode.getNewLabel();
    }

    /**
     * Sets the current bytecode position as the target for a label.
     *
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
     *
     * @return The for-loop end label
     */
    public int getForEndLabel() {
        assert forStack.peek() != null;
        return forStack.peek().forEndLabel;
    }

    /**
     * Gets the label for the current program end.
     *
     * @return The program end label
     */
    public int getProgEndLabel() {
        assert forStack.peek() != null;
        return forStack.peek().progEndLabel;
    }

    /**
     * Sets the current bytecode position as the target for the program end label.
     */
    public void setProgEndLabel() {
        assert forStack.peek() != null;
        bytecode.setLabel(forStack.peek().progEndLabel, bytecode.size());
    }

    /**
     * Sets the current bytecode position as the target for the for-loop end label.
     */
    public void setForEndLabel() {
        assert forStack.peek() != null;
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
     *
     * @param value The value to look up
     * @return The index of the constant
     */
    private int getConstIndex(IWenyanValue value) {
        return constTable.computeIfAbsent(value, bytecode::addConst);
    }

    /**
     * Gets the identifier index for a string, adding it if not present.
     *
     * @param identifier The identifier to look up
     * @return The index of the identifier
     */
    public int getIdentifierIndex(String identifier) {
        return identifierTable.computeIfAbsent(identifier, bytecode::addIdentifier);
    }

    private @Nullable ScopedValueHelper getScopedValue(String identifier) {
        for (Scope locals : scopeStack) {
            if (locals.varibles.containsKey(identifier)) {
                int index = locals.varibles.get(identifier);
                return new ScopedValueHelper(new ScopedValue(index, this.bytecode),
                        new WenyanBytecode.CapturedValue(index, true));
            }
        }
        // reach global, not found, stop
        if (parent == null) return null;
        // recursive
        var scoped = parent.getScopedValue(identifier);
        // not found, i.e. global
        if (scoped == null) return null;
        int index = capturedValueTable.computeIfAbsent(scoped.scopedValue(), ignore -> bytecode.addCapturedValue(scoped.capturedValue));
        return new ScopedValueHelper(scoped.scopedValue,
                new WenyanBytecode.CapturedValue(index, false));
    }

    public String getSourceCode() {
        return bytecode.getSourceCode();
    }

    /**
     * Enters a new debug context.
     *
     * @param line         Line number
     * @param column       Column number
     * @param contentStart Start index
     * @param contentEnd   End index
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

    public void enterScope() {
        scopeStack.push(new Scope(localVariableCounter));
    }

    public void exitScope() {
        localVariableCounter = scopeStack.remove().varibleBase;
    }

    private static class Scope {
        private final int varibleBase;
        private final Map<String, Integer> varibles = new HashMap<>();

        public Scope(int varibleBase) {
            this.varibleBase = varibleBase;
        }
    }

    // if from local null, means it's local
    private record ScopedValue(int index, WenyanBytecode from) {}
    private record ScopedValueHelper(ScopedValue scopedValue, WenyanBytecode.CapturedValue capturedValue) {}
}
