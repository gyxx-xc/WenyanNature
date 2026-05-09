package indi.wenyan.content.entity;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.setup.language.ExceptionText;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.function.Supplier;

@Value
@Accessors(fluent = true)
public class ThrowEntityRequest implements IBaseHandleableRequest, IArgsRequest {
    IWenyanValue self;
    List<IWenyanValue> args;
    IWenyanRunner thread;
    RawHandlerPackage.IRawRequest rawRequest;

    Supplier<Boolean> packageModifier;
    @NonFinal
    boolean firstTime = true;

    public ThrowEntityRequest(IWenyanValue self, List<IWenyanValue> argsList, IWenyanRunner thread, RawHandlerPackage.IRawRequest rawRequest, Supplier<Boolean> booleanSupplier) {
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
                throw new WenyanException(ExceptionText.DeviceRemoved.string());
            }
            firstTime = false;
        }
        return rawRequest.handle(context, this);
    }
}
