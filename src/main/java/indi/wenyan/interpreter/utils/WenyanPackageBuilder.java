package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.WenyanFunction;
import indi.wenyan.interpreter.structure.values.WenyanNativeValue;
import indi.wenyan.interpreter.structure.values.WenyanObjectType;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
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

    public WenyanPackageBuilder constant(String name, WenyanType type, Object value) {
        environment.setVariable(name, new WenyanNativeValue(type, value, true));
        return this;
    }

    public WenyanPackageBuilder constant(String name, WenyanNativeValue value) {
        environment.setVariable(name, value);
        return this;
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function,
                                         WenyanType returnType, WenyanType[] argTypes) {
        return function(name, (self, args) -> {
            Object[] newArgs = new Object[args.size()];
            for (int i = 0; i < argTypes.length; i++)
                newArgs[i] = args.get(i).casting(argTypes[i]).getValue();
            return new WenyanNativeValue(returnType, function.apply(newArgs), true);
        }, new WenyanType[0]);
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function, WenyanType valueType) {
        return function(name, (self, args) -> {
            Object[] newArgs = new Object[args.size()];
            for (int i = 0; i < args.size(); i++)
                newArgs[i] = args.get(i).casting(valueType).getValue();
            return new WenyanNativeValue(valueType, function.apply(newArgs), true);
        }, new WenyanType[0]);
    }

    public WenyanPackageBuilder function(String name, LocalCallHandler.LocalFunction function) {
        return function(name, function, new WenyanType[0]);
    }

    public WenyanPackageBuilder function(String[] name, LocalCallHandler.LocalFunction function) {
        for (String n : name) {
            function(n, function);
        }
        return this;
    }

    public WenyanPackageBuilder function(String name, LocalCallHandler.LocalFunction function, WenyanType[] argTypes) {
        return function(name, new LocalCallHandler(function), argTypes);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall) {
        return function(name, javacall, new WenyanType[0]);
    }

    public WenyanPackageBuilder function(String[] name, JavacallHandler javacall) {
        for (String n : name) {
            function(n, javacall);
        }
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall, WenyanType[] argTypes) {
        environment.setVariable(name, new WenyanNativeValue(WenyanFunction.TYPE, javacall, true));
        return this;
    }

    public WenyanPackageBuilder object(String name, WenyanObjectType objectType) {
        environment.setVariable(name, new WenyanNativeValue(WenyanObjectType.TYPE, objectType, true));
        return this;
    }

    public static LocalCallHandler.LocalFunction reduceWith(ReduceFunction function) {
        return (self, args) -> {
            if (args.size() <= 1)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            WenyanNativeValue value = args.getFirst();
            for (int i = 1; i < args.size(); i++) {
                value = function.apply(value, args.get(i));
            }
            return value;
        };
    }

    public static LocalCallHandler.LocalFunction boolBinaryOperation(java.util.function.BiFunction<Boolean, Boolean, Boolean> function) {
        return (self, args) -> {
            if (args.size() != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            return new WenyanNativeValue(WenyanBoolean.TYPE,
                    function.apply((boolean) args.get(0).casting(WenyanBoolean.TYPE).getValue(),
                            (boolean) args.get(1).casting(WenyanBoolean.TYPE).getValue()),
                    true);
        };
    }

    public static LocalCallHandler.LocalFunction compareOperation(CompareFunction function) {
        return (self, args) -> {
            if (args.size() != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            return new WenyanNativeValue(WenyanBoolean.TYPE,
                    function.apply(args.get(0), args.get(1)), true);
        };
    }

    @FunctionalInterface
    public interface ReduceFunction {
        WenyanNativeValue apply(WenyanNativeValue a, WenyanNativeValue b) throws WenyanException.WenyanThrowException;
    }

    @FunctionalInterface
    public interface CompareFunction {
        boolean apply(WenyanNativeValue a, WenyanNativeValue b) throws WenyanException.WenyanThrowException;
    }
}
