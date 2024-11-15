package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashMap;

public class WenyanFunctionEnvironment {
    private final WenyanFunctionEnvironment parentEnvironment;
    private final HashMap<String, WenyanValue> variables;
    private final HashMap<FunctionSign, WenyanRParser.Function_define_statementContext> functions;
    public final WenyanStack resultStack;

    public WenyanFunctionEnvironment(WenyanFunctionEnvironment parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.resultStack = new WenyanStack();
    }

    public WenyanValue getVariable(String id) throws WenyanException.WenyanVarException {
        if (variables.containsKey(id)) {
            return variables.get(id);
        } else if (parentEnvironment != null) {
            return parentEnvironment.getVariable(id);
        } else {
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.variable_not_found_").getString()+id);
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
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.function_not_found_").getString()+sign.name());
        }
    }

    public void importEnvironment(WenyanFunctionEnvironment environment) {
        variables.putAll(environment.variables);
        functions.putAll(environment.functions);
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
