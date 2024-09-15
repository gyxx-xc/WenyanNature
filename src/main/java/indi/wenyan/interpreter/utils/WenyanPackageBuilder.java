package indi.wenyan.interpreter.utils;

import java.util.function.Function;

public class WenyanPackageBuilder {
    private final WenyanFunctionEnvironment environment = new WenyanFunctionEnvironment();

    public static WenyanPackageBuilder create() {
        return new WenyanPackageBuilder();
    }

    public WenyanPackageBuilder environment(WenyanFunctionEnvironment environment) {
        this.environment.importEnvironment(environment);
        return this;
    }

    public WenyanFunctionEnvironment build() {
        return environment;
    }

    public WenyanPackageBuilder constant(String name, WenyanValue.Type type, Object value) {
        environment.setVariable(name, new WenyanValue(type, value, true));
        return this;
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function,
                                         WenyanValue.Type returnType, WenyanValue.Type[] argTypes) {
        return function(name, args -> {
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < argTypes.length; i++)
                newArgs[i] = args[i].casting(argTypes[i]).getValue();
            return new WenyanValue(returnType, function.apply(newArgs), true);
        }, new WenyanValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function, WenyanValue.Type valueType) {
        return function(name, args -> {
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++)
                newArgs[i] = args[i].casting(valueType).getValue();
            return new WenyanValue(valueType, function.apply(newArgs), true);
        }, new WenyanValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function) {
        return function(name, function, new WenyanValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function, WenyanValue.Type[] argTypes) {
        return function(name, new JavacallHandler(function), argTypes);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall) {
        return function(name, javacall, new WenyanValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall, WenyanValue.Type[] argTypes) {
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(name, argTypes);
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        environment.setFunction(sign, javacall);
        return this;
    }
}
