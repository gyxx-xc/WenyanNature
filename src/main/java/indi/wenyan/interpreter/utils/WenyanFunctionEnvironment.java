package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class WenyanFunctionEnvironment {
    private final WenyanFunctionEnvironment parentEnvironment;
    private final HashMap<String, WenyanValue> variables;
    private final HashMap<FunctionSign, WenyanRParser.Function_define_statementContext> functions;
    public final Stack<WenyanValue> resultStack;

    public WenyanFunctionEnvironment(WenyanFunctionEnvironment parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.resultStack = new Stack<>();
    }

    public WenyanFunctionEnvironment() {
        this(null);
    }

    public WenyanValue getVariable(String id) throws WenyanException.WenyanVarException {
        if (variables.containsKey(id)) {
            return variables.get(id);
        } else if (parentEnvironment != null) {
            return parentEnvironment.getVariable(id);
        } else {
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.variable_not_found").getString());
        }
    }

    public void setVariable(String id, WenyanValue value) {
        variables.put(id, value);
    }

    public void setFunction(FunctionSign sign, WenyanRParser.Function_define_statementContext functions) {
        this.functions.put(sign, functions);
    }

    public WenyanRParser.Function_define_statementContext getFunction(FunctionSign sign) throws WenyanException.WenyanVarException {
        if (functions.containsKey(sign)) {
            return functions.get(sign);
        } else if (parentEnvironment != null) {
            return parentEnvironment.getFunction(sign);
        } else {
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.function_not_found").getString());
        }
    }

    public WenyanFunctionEnvironment importEnvironment(WenyanFunctionEnvironment environment) {
        variables.putAll(environment.variables);
        functions.putAll(environment.functions);
        return this;
    }

    public record FunctionSign(String name, WenyanValue.Type[] argTypes) {
        public boolean equals(Object obj) {
            if (obj instanceof FunctionSign sign) {
                return name.equals(sign.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(functions.keySet().toArray());
    }
}
