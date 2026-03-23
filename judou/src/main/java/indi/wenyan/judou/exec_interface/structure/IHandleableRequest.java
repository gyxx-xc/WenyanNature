package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;

public interface IHandleableRequest {
    IWenyanRunner thread();

    boolean run(IWenyanPlatform platform, IHandleContext context);
}
