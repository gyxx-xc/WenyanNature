package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFuture;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

public class AwaitCallHandler implements IJavacallHandler {
    public static final AwaitCallHandler INSTANCE = new AwaitCallHandler();

    private AwaitCallHandler() {
    }

    @Override
    public void call(IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList) throws WenyanException {
        if (argsList.size() != 1)
            throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, argsList.size()));
        if (argsList.getFirst().is(WenyanInteger.TYPE)) {
            // wait n tick, use request(need tick information)
            thread.platform().receive(new AwaitRequest(thread, argsList.getFirst().as(WenyanInteger.TYPE).value()));
            thread.block();
        } else if (argsList.getFirst().is(WenyanBuiltinFuture.TYPE)) {
            boolean needBlock = argsList.getFirst().as(WenyanBuiltinFuture.TYPE).addWaitingThread(thread);
            if (needBlock) thread.block();
        } else {
            throw new WenyanException.WenyanVarException(JudouExceptionText.InvalidArgumentType.string());
        }
    }

    public static class AwaitRequest implements IBaseHandleableRequest {
        @Accessors(fluent = true)
        @Getter
        private final IWenyanRunner thread;
        private int life;

        private AwaitRequest(IWenyanRunner thread, int life) {
            this.thread = thread;
            this.life = life;
        }

        @Override
        public boolean handle(IHandleContext context) throws WenyanException {
            if (life-- <= 0) {
                thread().getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
                thread().unblock();
                return true;
            }
            return false;
        }
    }
}
