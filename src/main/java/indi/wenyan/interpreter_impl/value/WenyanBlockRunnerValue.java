package indi.wenyan.interpreter_impl.value;

import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.runner.BlockRequest;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.content.entity.ThrowEntityContext;
import indi.wenyan.content.entity.ThrowRunnerEntity;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanObject;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanFuture;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFunction;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.language.ExceptionText;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.function.Consumer;

import static indi.wenyan.setup.language.ExceptionText.NotFindFu;

public record WenyanBlockRunnerValue(RunnerBlockEntity entity) implements IWenyanObject, IRequestCallHandler {
    public static final WenyanType<WenyanBlockRunnerValue> TYPE = new WenyanType<>(JudouTypeText.CodeExecutor.string(), WenyanBlockRunnerValue.class);

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        throw new WenyanUnreachedException();
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self,
                                         List<IWenyanValue> argsList, Consumer<IWenyanValue> onReturn) throws WenyanException {
        return new IBaseHandleableRequest() {
            @Override
            public IWenyanRunner thread() {
                return thread;
            }

            @Override
            public boolean handle(IHandleContext context) throws WenyanException {
                if (entity.isRemoved())
                    throw new WenyanException(ExceptionText.DeviceRemoved.string());
                switch (context) {
                    case BlockRequest.BlockContext(_, BlockPos pos1, _) -> {
                        if (pos1.distChessboard(entity.getBlockPos()) > WenyanConfig.getRunnerRange())
                            throw new WenyanException(ExceptionText.OutOfRange.string());
                    }
                    case ThrowEntityContext(ThrowRunnerEntity entity1) -> {
                        if (BlockPos.containing(entity1.getPosition(0)).distChessboard(entity.getBlockPos()) >
                                WenyanConfig.getRunnerRange())
                            throw new WenyanException(ExceptionText.OutOfRange.string());
                    }
                    default -> {}
                }

                var function = argsList.getFirst().as(WenyanBuiltinFunction.TYPE);
                if (entity.isRemoved())
                    throw new WenyanException(NotFindFu.string());
                if (entity.getLevel() instanceof ServerLevel serverLevel && context instanceof BlockRequest.BlockContext(_, BlockPos pos, _))
                    ICommunicateHolder.blockAddCommunicateServer(serverLevel, pos,
                            entity.getBlockPos().subtract(pos));
                var future = new WenyanFuture();
                WenyanFrame newRuntime = function.getNewRuntime(self, argsList.subList(1, argsList.size()), null, future::onRunnerReturn);
                if (!entity.newThread(newRuntime))
                    throw new WenyanException(ExceptionText.CantStart.string(entity.getPlatformName()));


                onReturn.accept(future);
                thread.unblock();
                return true;
            }
        };
    }
}
