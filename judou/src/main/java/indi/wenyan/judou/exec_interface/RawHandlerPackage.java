package indi.wenyan.judou.exec_interface;

import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.Map;
import java.util.function.Supplier;

public record RawHandlerPackage
        (Map<String, IWenyanValue> variables,
         Map<String, Supplier<IHandleableRequest.IRawRequest>> functions) {
}
