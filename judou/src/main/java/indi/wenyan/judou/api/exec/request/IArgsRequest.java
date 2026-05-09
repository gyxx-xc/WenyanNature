package indi.wenyan.judou.api.exec.request;

import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

public interface IArgsRequest extends IHandleableRequest {
    IWenyanValue self();
    List<IWenyanValue> args();
}
