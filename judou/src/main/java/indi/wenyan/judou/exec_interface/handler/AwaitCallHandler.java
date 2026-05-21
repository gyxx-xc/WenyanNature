package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.exec.ICrossFunctionExecutable;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public enum AwaitCallHandler implements IJavacallHandler, ICrossFunctionExecutable {
    INSTANCE;

    @Override
    public void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList, Consumer<IWenyanValue> onReturn) throws WenyanException {
        if (argsList.size() != 1)
            throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, argsList.size()));
        if (argsList.getFirst().is(WenyanInteger.TYPE)) {
            // wait n tick, use request(need tick information)
            thread.platform().receive(new AwaitRequest(thread, onReturn, argsList.getFirst().as(WenyanInteger.TYPE).value()));
            thread.block();
        } else if (argsList.getFirst().is(WenyanBuiltinFuture.TYPE)) {
            WenyanBuiltinFuture future = argsList.getFirst().as(WenyanBuiltinFuture.TYPE);
            if (future.getReturnValue() != null) onReturn.accept(future.getReturnValue());
            else {
                thread.block();
                future.addWaiting(result -> {
                    try {
                        onReturn.accept(result);
                        thread.unblock();
                    } catch (WenyanUnreachedException ignore) {
                        // should not happen
                        // or maybe? if the program stopped when waiting, ignore it then.
                    }
                });
            }
        } else {
            throw new WenyanException.WenyanVarException(JudouExceptionText.InvalidArgumentType.string());
        }
    }

    public static class AwaitRequest implements IBaseHandleableRequest {
        @Accessors(fluent = true)
        @Getter
        private final IWenyanRunner thread;
        private final Consumer<IWenyanValue> onReturn;
        private int life;

        private AwaitRequest(IWenyanRunner thread, Consumer<IWenyanValue> onReturn, int life) {
            this.thread = thread;
            this.onReturn = onReturn;
            this.life = life;
        }

        @Override
        public boolean handle(IHandleContext context) throws WenyanException {
            if (life-- <= 0) {
                onReturn.accept(WenyanNull.NULL);
                thread().unblock();
                return true;
            }
            return false;
        }
    }
}
