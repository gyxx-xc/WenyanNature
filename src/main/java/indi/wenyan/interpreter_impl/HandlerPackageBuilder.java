package indi.wenyan.interpreter_impl;

import indi.wenyan.content.block.power.PowerBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.BaseHandleableRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.WenyanPackageBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static indi.wenyan.content.block.runner.RunnerBlockEntity.DEVICE_SEARCH_RANGE;

/**
 * Builder for creating WenyanPackage values
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class HandlerPackageBuilder {
    // with support of wenyan package
    private final Map<String, IWenyanValue> variables = new HashMap<>();
    private final Map<String, Supplier<BaseHandleableRequest.IRawRequest>> functions = new HashMap<>();

    /**
     * Creates a new package builder
     *
     * @return A new package builder instance
     */
    @Contract(" -> new")
    public static @NotNull HandlerPackageBuilder create() {
        return new HandlerPackageBuilder();
    }

    public HandlerPackageBuilder nativeVariables(UnaryOperator<WenyanPackageBuilder> builder) {
        variables.putAll(builder.apply(WenyanPackageBuilder.create()).build().variables());
        return this;
    }

    /**
     * Adds all variables from an existing environment
     *
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
     *
     * @return The built package
     */
    public RawHandlerPackage build() {
        return new RawHandlerPackage(variables, functions);
    }

    public HandlerPackageBuilder handler(String name, Supplier<BaseHandleableRequest.IRawRequest> function) {
        functions.put(name, function);
        return this;
    }

    public HandlerPackageBuilder handler(String name, BaseHandleableRequest.IRawRequest function) {
        return handler(name, () -> function);
    }

    public HandlerPackageBuilder handler(String name, HandlerReturnFunction function) {
        return handler(name, (BaseHandleableRequest.IRawRequest) (context, request) -> {
            IWenyanValue value = function.handle(context, request);
            request.thread().currentRuntime().pushReturnValue(value);
            return true;
        });
    }

    public HandlerPackageBuilder handler(String name, HandlerSimpleFunction function) {
        return handler(name, (BaseHandleableRequest.IRawRequest) (context, request) -> {
            IWenyanValue value = function.handle(request);
            request.thread().currentRuntime().pushReturnValue(value);
            return true;
        });
    }

    public HandlerPackageBuilder handler(String name, ImportFunction function) {
        return handler(name, (BaseHandleableRequest.IRawRequest) (context, request) -> {
            String packageName = request.args().getFirst().as(WenyanString.TYPE).value();
            WenyanPackage execPackage = function.getPackage(context, packageName);
            if (request.args().size() == 1) {
                request.thread().currentRuntime().setVariable(packageName, execPackage);
                request.thread().currentRuntime().getResultStack().push(execPackage);
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
    }

    public HandlerPackageBuilder handler(String name, int power, HandlerReturnFunction function) {
        return handler(name, () -> new BaseHandleableRequest.IRawRequest() {
            int acquired = 0;

            @Override
            public boolean handle(@NotNull IHandleContext context, @NotNull IHandleableRequest request) throws WenyanThrowException {
                boolean hasDevice = false;
                if (request.thread().platform() instanceof RunnerBlockEntity entity) {
                    for (BlockPos b : BlockPos.betweenClosed(
                            entity.getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                            entity.getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
                        assert entity.getLevel() != null;
                        if (entity.getLevel().getBlockEntity(b) instanceof PowerBlockEntity executor) {
                            hasDevice = true;
                            acquired += executor.require(power);
                        }
                    }
                }
                if (!hasDevice)
                    // STUB
                    request.thread().platform().handleError("need power");
                if (acquired < power) {
                    return false;
                } else {
                    IWenyanValue value = function.handle(context, request);
                    request.thread().currentRuntime().pushReturnValue(value);
                    return true;
                }
            }
        });
    }

    @FunctionalInterface
    public interface HandlerFunction {
        boolean handle(@NotNull IHandleContext context, @NotNull IHandleableRequest request) throws WenyanThrowException;
    }

    @FunctionalInterface
    public interface HandlerReturnFunction {
        IWenyanValue handle(@NotNull IHandleContext context, @NotNull IHandleableRequest request) throws WenyanThrowException;
    }

    @FunctionalInterface
    public interface HandlerSimpleFunction {
        IWenyanValue handle(@NotNull IHandleableRequest request) throws WenyanThrowException;
    }

    @FunctionalInterface
    public interface ImportFunction {
        WenyanPackage getPackage(@NotNull IHandleContext context, @NotNull String name) throws WenyanThrowException;
    }
}
