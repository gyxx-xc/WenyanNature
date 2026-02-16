package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

public interface IHandleableRequest {
    WenyanThread thread();
    IWenyanValue self();
    List<IWenyanValue> args();

    boolean run(IWenyanPlatform platform, IHandleContext context);
}
