package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface BaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanException;

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            return handle(context);
        } catch (WenyanException e) {
            WenyanRunner.dieWithException(thread(), e);
            return true;
        } catch (RuntimeException e) {
            WenyanRunner.dieWithException(thread(), new WenyanUnreachedException.WenyanUnexceptedException(e));
            return true;
        }
    }

    @FunctionalInterface
    interface IRawRequest {
        boolean handle(IHandleContext context, IArgsRequest request) throws WenyanException;
    }
}
