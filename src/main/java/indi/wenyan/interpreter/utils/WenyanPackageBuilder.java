package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanValue;

import java.util.function.Function;

@SuppressWarnings("unused")
public class WenyanPackageBuilder {
    private final WenyanRuntime environment = new WenyanRuntime(null);

    public static WenyanPackageBuilder create() {
        return new WenyanPackageBuilder();
    }

    public WenyanPackageBuilder environment(WenyanRuntime environment) {
        this.environment.importEnvironment(environment);
        return this;
    }

    public WenyanRuntime build() {
        return environment;
    }

    public WenyanPackageBuilder constant(String name, WenyanValue.Type type, Object value) {
        environment.setVariable(name, new WenyanValue(type, value, true));
        return this;
    }

    public WenyanPackageBuilder constant(String name, WenyanValue value) {
        environment.setVariable(name, value);
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
        WenyanValue.FunctionSign sign = new WenyanValue.FunctionSign(name, argTypes, new JavaCallCodeWarper(javacall));
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        return this;
    }
}
