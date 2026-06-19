package indi.wenyan.judou.api.exec.request;

import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

/// Simpler {@link IHandleableRequest}: only implement {@link #handle}, error handling is automatic.
public interface IBaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanException;

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            return handle(context);
        } catch (WenyanException e) {
            WenyanFrame.dieWithException(thread(), e);
            return true;
        } catch (RuntimeException e) {
            WenyanFrame.dieWithException(thread(), new WenyanUnreachedException.WenyanUnexceptedException(e));
            return true;
        }
    }
}
