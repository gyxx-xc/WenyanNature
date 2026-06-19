package indi.wenyan.judou.api.exec.request;

import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.runtime.IWenyanRunner;

/// A request queued for asynchronous execution on the game thread.
public interface IHandleableRequest {
    IWenyanRunner thread();

    boolean run(IWenyanPlatform platform, IHandleContext context);
}
