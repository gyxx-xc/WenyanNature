package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;

public interface BaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanThrowException;

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            noticePlatform(platform, context);
            return handle(context);
        } catch (WenyanThrowException e) {
            thread().dieWithException(e);
            return true;
        } catch (Exception e) {
            thread().dieWithException(new WenyanException.WenyanUnreachedException());
            return true;
        }
    }

    @FunctionalInterface
    interface IRawRequest {
        boolean handle(IHandleContext context, IHandleableRequest request) throws WenyanThrowException;
    }

    default void noticePlatform(IWenyanPlatform platform, IHandleContext context) throws WenyanThrowException {
        platform.notice(this, context);
    }
}
