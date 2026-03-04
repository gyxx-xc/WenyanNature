package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;

public interface IHandleableRequest {
    WenyanRunner thread();

    boolean run(IWenyanPlatform platform, IHandleContext context);
}
