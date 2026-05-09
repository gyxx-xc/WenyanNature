package indi.wenyan.judou.api.exec.structure;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.Map;
import java.util.function.Supplier;

public record RawHandlerPackage
        (Map<String, IWenyanValue> variables,
         Map<String, Supplier<IRawRequest>> functions) {
    @FunctionalInterface
    public interface IRawRequest {
        boolean handle(IHandleContext context, IArgsRequest request) throws WenyanException;
    }
}
