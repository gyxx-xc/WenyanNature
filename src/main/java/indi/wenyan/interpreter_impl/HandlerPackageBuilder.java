package indi.wenyan.interpreter_impl;

import indi.wenyan.content.block.power.PowerBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.ChineseUtils;
import indi.wenyan.judou.api.utils.WenyanPackageBuilder;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanPackage;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.setup.config.WenyanConfig;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Builder for creating WenyanPackage values
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class HandlerPackageBuilder {
    // with support of wenyan package
    private final Map<String, IWenyanValue> variables = new HashMap<>();
    private final Map<String, Supplier<RawHandlerPackage.IRawRequest>> functions = new HashMap<>();

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

    public HandlerPackageBuilder handler(String name, Supplier<RawHandlerPackage.IRawRequest> function) {
        switch (WenyanConfig.getJudouConfigProvider().symbolConversion()) {
            case TRADITIONAL -> functions.put(name, function);
            case SIMPLIFIED -> functions.put(ChineseUtils.toSimplifiedVar(name), function);
            case BOTH -> {
                functions.put(name, function);
                functions.put(ChineseUtils.toSimplifiedVar(name), function);
            }
        }
        return this;
    }

    public HandlerPackageBuilder handler(String name, RawHandlerPackage.IRawRequest function) {
        return handler(name, () -> function);
    }

    public HandlerPackageBuilder handler(String name, HandlerReturnFunction function) {
        return handler(name, (context, request, onReturn) -> {
            IWenyanValue value = function.handle(context, request);
            onReturn.accept(value);
            request.thread().unblock();
            return true;
        });
    }

    public HandlerPackageBuilder handler(String name, HandlerSimpleFunction function) {
        return handler(name, (context, request, onReturn) -> {
            IWenyanValue value = function.handle(request);
            onReturn.accept(value);
            request.thread().unblock();
            return true;
        });
    }

    @Deprecated
    public HandlerPackageBuilder handler(String name, int power, HandlerReturnFunction function) {
        return handler(name, () -> new RawHandlerPackage.IRawRequest() {
            private final int range = WenyanConfig.getRunnerRange();
            int acquired = 0;

            @Override
            public boolean handle(@NotNull IHandleContext context, @NotNull IArgsRequest request, Consumer<IWenyanValue> onReturn) throws WenyanException {
                boolean hasDevice = false;
                if (request.thread().platform() instanceof RunnerBlockEntity entity) {
                    for (BlockPos b : BlockPos.betweenClosed(
                            entity.getBlockPos().offset(range, -range, range),
                            entity.getBlockPos().offset(-range, range, -range))) {
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
                    onReturn.accept(value);
                    request.thread().unblock();
                    return true;
                }
            }
        });
    }

    @FunctionalInterface
    public interface HandlerFunction {
        boolean handle(@NotNull IHandleContext context, @NotNull IArgsRequest request) throws WenyanException;
    }

    @FunctionalInterface
    public interface HandlerReturnFunction {
        IWenyanValue handle(@NotNull IHandleContext context, @NotNull IArgsRequest request) throws WenyanException;
    }

    @FunctionalInterface
    public interface HandlerSimpleFunction {
        IWenyanValue handle(@NotNull IArgsRequest request) throws WenyanException;
    }
}
