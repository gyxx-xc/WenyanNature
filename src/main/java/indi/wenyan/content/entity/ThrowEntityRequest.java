package indi.wenyan.content.entity;

import indi.wenyan.judou.exec_interface.structure.BaseHandleableRequest;
import indi.wenyan.judou.exec_interface.structure.IArgsRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.function.Supplier;

@Value
@Accessors(fluent = true)
public class ThrowEntityRequest implements BaseHandleableRequest, IArgsRequest {
    IWenyanValue self;
    List<IWenyanValue> args;
    IWenyanRunner thread;
    IRawRequest rawRequest;

    Supplier<Boolean> packageModifier;
    @NonFinal
    boolean firstTime = true;

    public ThrowEntityRequest(IWenyanValue self, List<IWenyanValue> argsList, IWenyanRunner thread, IRawRequest rawRequest, Supplier<Boolean> booleanSupplier) {
        this.self = self;
        this.args = argsList;
        this.thread = thread;
        this.rawRequest = rawRequest;
        this.packageModifier = booleanSupplier;
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        if (firstTime) {
            if (!packageModifier.get()) {
                throw new WenyanException("package not found");
            }
            firstTime = false;
        }
        return rawRequest.handle(context, this);
    }
}
