package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

public interface IArgsRequest extends IHandleableRequest {
    IWenyanValue self();
    List<IWenyanValue> args();
}
