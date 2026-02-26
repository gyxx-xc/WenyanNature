package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface BaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanException;

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            noticePlatform(platform, context);
            return handle(context);
        } catch (WenyanException e) {
            thread().dieWithException(e);
            return true;
        } catch (RuntimeException e) {
            thread().dieWithException(new WenyanUnreachedException.WenyanUnexceptedException(e));
            return true;
        }
    }

    @FunctionalInterface
    interface IRawRequest {
        boolean handle(IHandleContext context, IHandleableRequest request) throws WenyanException;
    }

    default void noticePlatform(IWenyanPlatform platform, IHandleContext context) throws WenyanException {
        platform.notice(this, context);
    }
}
