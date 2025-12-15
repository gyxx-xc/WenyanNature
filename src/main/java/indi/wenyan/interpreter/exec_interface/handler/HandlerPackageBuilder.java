package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Builder for creating WenyanPackage values
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class HandlerPackageBuilder {
    // with support of wenyan package
    private final Map<String, IWenyanValue> variables = new HashMap<>();
    private final Map<String, HandlerFunction> functions = new HashMap<>();

    /**
     * Creates a new package builder
     * @return A new package builder instance
     */
    @Contract(" -> new")
    public static @NotNull HandlerPackageBuilder create() {
        return new HandlerPackageBuilder();
    }

    public HandlerPackageBuilder nativeVariables(Function<WenyanPackageBuilder, WenyanPackageBuilder> builder) {
        variables.putAll(builder.apply(WenyanPackageBuilder.create()).build().variables());
        return this;
    }

    /**
     * Adds all variables from an existing environment
     * @param environment Environment to include
     * @return This builder
     */
    @Contract("_ -> this")
    public HandlerPackageBuilder environment(@NotNull WenyanPackage environment) {
        variables.putAll(environment.variables());
        return this;
    }

    /**
     * Builds the package
     * @return The built package
     */
    public RawHandlerPackage build() {
        return new RawHandlerPackage(variables, functions);
    }

    public HandlerPackageBuilder handler(String name, HandlerFunction function) {
        functions.put(name, function);
        return this;
    }

    public HandlerPackageBuilder handler(String name, HandlerReturnFunction function) {
        functions.put(name, (context, request) -> {
            IWenyanValue value = function.handle(context, request);
            request.thread().currentRuntime().processStack.push(value);
            return true;
        });
        return this;
    }

    public HandlerPackageBuilder handler(String name, HandlerSimpleFunction function) {
        functions.put(name, (context, request) -> {
            IWenyanValue value = function.handle(request);
            request.thread().currentRuntime().processStack.push(value);
            return true;
        });
        return this;
    }

    public HandlerPackageBuilder handler(String name, ImportFunction function) {
        functions.put(name, (context, request) -> {
            String packageName = request.args().getFirst().as(WenyanString.TYPE).value();
            WenyanPackage execPackage = function.getPackage(context, packageName);
            if (request.args().size() == 1) {
                request.thread().currentRuntime().setVariable(packageName, execPackage);
                request.thread().currentRuntime().resultStack.push(execPackage);
            } else {
                for (IWenyanValue arg : request.args().subList(1, request.args().size())) {
                    String id = arg.as(WenyanString.TYPE).value();
                    // not found error will throw inside getAttribute
                    request.thread().currentRuntime().setVariable(id,
                            execPackage.getAttribute(id));
                }
            }
            return true;
        });
        return this;
    }

    @FunctionalInterface
    public interface HandlerFunction {
        boolean handle(@NotNull IHandleContext context, @NotNull JavacallRequest request) throws WenyanException.WenyanThrowException;
    }

    @FunctionalInterface
    public interface HandlerReturnFunction {
        IWenyanValue handle(@NotNull IHandleContext context, @NotNull JavacallRequest request) throws WenyanException.WenyanTypeException;
    }

    @FunctionalInterface
    public interface HandlerSimpleFunction {
        IWenyanValue handle(@NotNull JavacallRequest request) throws WenyanException.WenyanThrowException;
    }

    @FunctionalInterface
    public interface ImportFunction {
        WenyanPackage getPackage(@NotNull IHandleContext context, @NotNull String name) throws WenyanException.WenyanThrowException;
    }

    public record RawHandlerPackage
            (Map<String, IWenyanValue> variables, Map<String, HandlerFunction> functions) { }
}
