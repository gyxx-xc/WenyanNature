package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.*;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
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

    public WenyanPackageBuilder constant(String name, WenyanValue value) {
        environment.setVariable(name, value);
        return this;
    }

    public WenyanPackageBuilder
    doubleFunction(String name, Function<List<Double>, Double> function) {
        return function(name, (self, args) -> {
            List<Double> newArgs = new ArrayList<>();
            for (WenyanValue arg : args) {
                newArgs.add(arg.as(WenyanDouble.TYPE).value);
            }
            return new WenyanDouble(function.apply(newArgs));
        }, new WenyanType[0]);
    }

    public WenyanPackageBuilder
    intFunction(String name, Function<List<Integer>, Integer> function) {
        return function(name, (self, args) -> {
            List<Integer> newArgs = new ArrayList<>();
            for (WenyanValue arg : args) {
                newArgs.add(arg.as(WenyanInteger.TYPE).value);
            }
            return new WenyanInteger(function.apply(newArgs));
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

    public WenyanPackageBuilder function(String name, LocalCallHandler.LocalFunction function, WenyanType<?>[] argTypes) {
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

    public WenyanPackageBuilder function(String name, JavacallHandler javacall, WenyanType<?>[] argTypes) {
        environment.setVariable(name, javacall);
        return this;
    }

    public WenyanPackageBuilder object(String name, WenyanObjectType objectType) {
        environment.setVariable(name, objectType);
        return this;
    }

    public static LocalCallHandler.LocalFunction reduceWith(ReduceFunction function) {
        return (self, args) -> {
            if (args.size() <= 1)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            WenyanValue value = args.getFirst();
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
            return new WenyanBoolean(function.apply(args.get(0).as(WenyanBoolean.TYPE).value,
                    args.get(1).as(WenyanBoolean.TYPE).value));
        };
    }

    public static LocalCallHandler.LocalFunction compareOperation(CompareFunction function) {
        return (self, args) -> {
            if (args.size() != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            return new WenyanBoolean(function.apply(args.get(0), args.get(1)));
        };
    }

    @FunctionalInterface
    public interface ReduceFunction {
        WenyanValue apply(WenyanValue a, WenyanValue b) throws WenyanException.WenyanThrowException;
    }

    @FunctionalInterface
    public interface CompareFunction {
        boolean apply(WenyanValue a, WenyanValue b) throws WenyanException.WenyanThrowException;
    }
}
