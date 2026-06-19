package indi.wenyan.judou.api.exec.structure;

import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/// Batch registration of raw handlers and variables.
///
/// @param variables map of variable names to values
/// @param functions map of function names to supplier of raw request handlers
public record RawHandlerPackage
        (Map<String, IWenyanValue> variables,
         Map<String, Supplier<IRawRequest>> functions,
         Map<String, WenyanMetadata> metadata) {
    @FunctionalInterface
    public interface IRawRequest {
        boolean handle(IHandleContext context, IArgsRequest request, Consumer<IWenyanValue> onReturn) throws WenyanException;
    }
}
