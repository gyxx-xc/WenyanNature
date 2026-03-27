package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;

import static indi.wenyan.setup.language.ExceptionText.LockHoldAlready;
import static indi.wenyan.setup.language.ExceptionText.LockNotHold;

public class LockModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.SemaphoreModule;

    private IThreadHolder<WenyanProgramImpl.PCB> lockHolder;
    private final Queue<IThreadHolder<WenyanProgramImpl.PCB>> waitingThreads = new ArrayDeque<>();

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.SemaphoreModule$acquire, this::acquireSemaphoreHandler)
            .handler(WenyanSymbol.SemaphoreModule$release, this::releaseSemaphore)
            .build();

    private boolean acquireSemaphoreHandler(IHandleContext context, IHandleableRequest request) throws WenyanException {
        boolean locked = getBlockState().getValue(LockModuleBlock.LOCK_STATE);
        if (locked) {
            if (lockHolder == request.thread() || waitingThreads.contains(request.thread()))
                throw new WenyanException(LockHoldAlready.string());
            waitingThreads.add(request.thread());
        } else {
            assert level != null;
            level.setBlock(getBlockPos(), getBlockState().setValue(LockModuleBlock.LOCK_STATE, true), Block.UPDATE_CLIENTS);
            lockHolder = request.thread();
            request.thread().unblock();
        }
        request.thread().getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
        return true;
    }

    private @NotNull WenyanNull releaseSemaphore(IHandleableRequest request) throws WenyanException {
        boolean locked = getBlockState().getValue(LockModuleBlock.LOCK_STATE);
        if (!locked || lockHolder != request.thread()) {
            throw new WenyanException(LockNotHold.string());
        }
        if (waitingThreads.isEmpty()) {
            assert level != null;
            level.setBlock(getBlockPos(), getBlockState().setValue(LockModuleBlock.LOCK_STATE, false), Block.UPDATE_CLIENTS);
            lockHolder = null;
        } else {
            lockHolder = waitingThreads.poll();
            lockHolder.unblock();
        }
        return WenyanNull.NULL;
    }

    public LockModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOCK_MODULE_ENTITY.get(), pos, blockState);
    }
}
