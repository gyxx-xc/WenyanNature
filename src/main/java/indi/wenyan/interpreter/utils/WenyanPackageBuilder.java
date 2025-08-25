package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.content.handler.IJavacallHandler;
import indi.wenyan.content.handler.WenyanBuiltinFunction;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Builder for creating WenyanPackage values
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class WenyanPackageBuilder {
    /** Map of variables to include in the package */
    Map<String, IWenyanValue> variables = new HashMap<>();

    /**
     * Creates a new package builder
     * @return A new package builder instance
     */
    public static WenyanPackageBuilder create() {
        return new WenyanPackageBuilder();
    }

    /**
     * Adds all variables from an existing environment
     * @param environment Environment to include
     * @return This builder
     */
    public WenyanPackageBuilder environment(WenyanPackage environment) {
        variables.putAll(environment.getVariables());
        return this;
    }

    /**
     * Builds the package
     * @return The built package
     */
    public WenyanPackage build() {
        return new WenyanPackage(Map.copyOf(variables));
    }

    /**
     * Adds a constant to the package
     * @param name Name of the constant
     * @param value Value of the constant
     * @return This builder
     */
    public WenyanPackageBuilder constant(String name, IWenyanValue value) {
        variables.put(name, value);
        return this;
    }

    /**
     * Adds a function that operates on doubles
     * @param name Function name
     * @param function The function implementation
     * @return This builder
     */
    public WenyanPackageBuilder
    doubleFunction(String name, Function<List<Double>, Double> function) {
        return function(name, (self, args) -> {
            List<Double> newArgs = new ArrayList<>();
            for (IWenyanValue arg : args) {
                newArgs.add(arg.as(WenyanDouble.TYPE).value());
            }
            return new WenyanDouble(function.apply(newArgs));
        }, new WenyanType[0]);
    }

    /**
     * Adds a function that operates on integers
     * @param name Function name
     * @param function The function implementation
     * @return This builder
     */
    public WenyanPackageBuilder
    intFunction(String name, Function<List<Integer>, Integer> function) {
        return function(name, (self, args) -> {
            List<Integer> newArgs = new ArrayList<>();
            for (IWenyanValue arg : args) {
                newArgs.add(arg.as(WenyanInteger.TYPE).value());
            }
            return new WenyanInteger(function.apply(newArgs));
        }, new WenyanType[0]);
    }

    /**
     * Adds a builtin function to the package
     * @param name Function name
     * @param function The function implementation
     * @return This builder
     */
    public WenyanPackageBuilder function(String name, WenyanBuiltinFunction.BuiltinFunction function) {
        return function(name, function, new WenyanType[0]);
    }

    /**
     * Adds the same function under multiple names
     * @param name Array of function names
     * @param function The function implementation
     * @return This builder
     */
    public WenyanPackageBuilder function(String[] name, WenyanBuiltinFunction.BuiltinFunction function) {
        for (String n : name) {
            function(n, function);
        }
        return this;
    }

    /**
     * Adds a builtin function with argument type constraints
     * @param name Function name
     * @param function The function implementation
     * @param argTypes Array of required argument types
     * @return This builder
     */
    public WenyanPackageBuilder function(String name, WenyanBuiltinFunction.BuiltinFunction function, WenyanType<?>[] argTypes) {
        return function(name, new WenyanBuiltinFunction(function), argTypes);
    }

    /**
     * Adds a Java-implemented function to the package
     * @param name Function name
     * @param javacall The handler implementation
     * @return This builder
     */
    public WenyanPackageBuilder function(String name, IExecCallHandler javacall) {
        return function(name, javacall, new WenyanType[0]);
    }

    /**
     * Adds the same Java-implemented function under multiple names
     * @param name Array of function names
     * @param javacall The handler implementation
     * @return This builder
     */
    public WenyanPackageBuilder function(String[] name, IExecCallHandler javacall) {
        for (String n : name) {
            function(n, javacall);
        }
        return this;
    }

    /**
     * Adds a Java-implemented function with argument type constraints
     * @param name Function name
     * @param javacall The handler implementation
     * @param argTypes Array of required argument types
     * @return This builder
     */
    public WenyanPackageBuilder function(String name, IJavacallHandler javacall, WenyanType<?>[] argTypes) {
        variables.put(name, javacall);
        return this;
    }

    /**
     * Adds an object type to the package
     * @param name Object name
     * @param objectType The object type implementation
     * @return This builder
     */
    public WenyanPackageBuilder object(String name, IWenyanObjectType objectType) {
        variables.put(name, objectType);
        return this;
    }

    /**
     * Creates a function that reduces a list of arguments using a binary operation
     * @param function The binary operation to apply
     * @return A builtin function that reduces arguments
     */
    public static WenyanBuiltinFunction.BuiltinFunction reduceWith(ReduceFunction function) {
        return (self, args) -> {
            if (args.size() <= 1)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
            IWenyanValue value = args.getFirst();
            for (int i = 1; i < args.size(); i++) {
                value = function.apply(value, args.get(i));
            }
            return value;
        };
    }

    /**
     * Creates a function that applies a binary boolean operation
     * @param function The binary boolean operation
     * @return A builtin function that applies the operation
     */
    public static WenyanBuiltinFunction.BuiltinFunction boolBinaryOperation(BiFunction<Boolean, Boolean, Boolean> function) {
        return (self, args) -> {
            if (args.size() != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
            return new WenyanBoolean(function.apply(args.get(0).as(WenyanBoolean.TYPE).value(),
                    args.get(1).as(WenyanBoolean.TYPE).value()));
        };
    }

    /**
     * Creates a function that compares two values
     * @param function The comparison function
     * @return A builtin function that compares values
     */
    public static WenyanBuiltinFunction.BuiltinFunction compareOperation(CompareFunction function) {
        return (self, args) -> {
            if (args.size() != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
            return new WenyanBoolean(function.apply(args.get(0), args.get(1)));
        };
    }

    /**
     * Functional interface for binary operations on Wenyan values
     */
    @FunctionalInterface
    public interface ReduceFunction {
        IWenyanValue apply(IWenyanValue a, IWenyanValue b) throws WenyanException.WenyanThrowException;
    }

    /**
     * Functional interface for comparing Wenyan values
     */
    @FunctionalInterface
    public interface CompareFunction {
        boolean apply(IWenyanValue a, IWenyanValue b) throws WenyanException.WenyanThrowException;
    }
}
