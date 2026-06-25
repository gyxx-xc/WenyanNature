package indi.wenyan.content.block.runner;

import indi.wenyan.content.entity.ThrowEntityContext;
import indi.wenyan.content.entity.ThrowRunnerEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.language.ExceptionText;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

@Value
@Accessors(fluent = true)
public class BlockRequest implements IBaseHandleableRequest, IArgsRequest {
    IWenyanRunner thread;
    IWenyanValue self;
    List<IWenyanValue> args;

    Consumer<BlockPos> communicateConsumer;
    IWenyanBlockDevice device;
    BlockPos pos;
    RawHandlerPackage.IRawRequest request;
    Consumer<IWenyanValue> onReturn;

    @NonFinal
    boolean communicationShown = false;

    public BlockRequest(IWenyanRunner thread,
                        IWenyanValue self,
                        List<IWenyanValue> argsList,
                        IWenyanBlockDevice device,
                        RawHandlerPackage.IRawRequest request,
                        Consumer<BlockPos> communicateConsumer,
                        Consumer<IWenyanValue> onReturn) {
        this.thread = thread;
        this.self = self;
        this.args = argsList;
        this.device = device;
        this.pos = device.blockPos();
        this.request = request;
        this.communicateConsumer = communicateConsumer;
        this.onReturn = onReturn;
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        if (device().isRemoved())
            throw new WenyanException(ExceptionText.DeviceRemoved.string());
        switch (context) {
            case BlockContext(_, BlockPos pos1, _) -> {
                if (pos1.distChessboard(pos) > WenyanConfig.getRunnerRange())
                    throw new WenyanException(ExceptionText.OutOfRange.string());
            }
            case ThrowEntityContext(ThrowRunnerEntity entity) -> {
                if (BlockPos.containing(entity.getPosition(0)).distChessboard(pos) > WenyanConfig.getRunnerRange())
                    throw new WenyanException(ExceptionText.OutOfRange.string());
            }
            default -> {}
        }

        if (!communicationShown) {
            communicateConsumer.accept(device.blockPos());
            communicationShown = true;
        }
        return request.handle(context, this, onReturn);
    }

    public record BlockContext(Level level, BlockPos pos,
                               BlockState state) implements IHandleContext {
    }
}
