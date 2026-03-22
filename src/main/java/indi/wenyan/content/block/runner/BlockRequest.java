package indi.wenyan.content.block.runner;

import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.structure.BaseHandleableRequest;
import indi.wenyan.judou.exec_interface.structure.IArgsRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
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
public class BlockRequest implements BaseHandleableRequest, IArgsRequest {
    IWenyanRunner thread;
    IWenyanValue self;
    List<IWenyanValue> args;

    Consumer<BlockPos> communicateConsumer;
    IWenyanBlockDevice device;
    BlockPos pos;
    IRawRequest request;

    @NonFinal
    boolean communicationShown = false;

    public BlockRequest(IWenyanRunner thread,
                        IWenyanValue self,
                        List<IWenyanValue> argsList,
                        IWenyanBlockDevice device,
                        IRawRequest request,
                        Consumer<BlockPos> communicateConsumer) {
        this.thread = thread;
        this.self = self;
        this.args = argsList;
        this.device = device;
        this.pos = device.blockPos();
        this.request = request;
        this.communicateConsumer = communicateConsumer;
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        if (device().isRemoved())
            throw new WenyanException(ExceptionText.DeviceRemoved.string());
        if (!communicationShown) {
            communicateConsumer.accept(device.blockPos());
            communicationShown = true;
        }
        return request.handle(context, this);
    }

    public record BlockContext(Level level, BlockPos pos,
                               BlockState state) implements IHandleContext {
    }
}
