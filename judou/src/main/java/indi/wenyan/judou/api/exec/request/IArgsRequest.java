package indi.wenyan.judou.api.exec.request;

import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

/// A request carrying a self reference and arguments.
public interface IArgsRequest extends IHandleableRequest {
    IWenyanValue self();
    List<IWenyanValue> args();
}
