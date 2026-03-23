package indi.wenyan.content.block.additional_module.paper;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.runner.BlockRequest;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.IArgsRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.network.client.CommunicationLocationPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

import static indi.wenyan.judou.utils.language.JudouExceptionText.ArgsNumWrong;

/**
 * Entity for the blocking queue module.
 * Provides thread-safe queue operations for synchronization.
 * Uses manual blocking/unblocking mechanism similar to LockModuleEntity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockingQueueModuleEntity extends AbstractModuleEntity implements ICommunicateHolder {
    @Getter
    private final List<CommunicationEffect> communicates = new ArrayList<>();

    @Getter
    private final String basePackageName = WenyanSymbol.BlockingQueueModule;

    private final Queue<IWenyanValue> queue = new ArrayDeque<>();
    private final int capacity = 10; // Default capacity

    private final Queue<BlockedThread> waitingProducers = new ArrayDeque<>();
    private final Queue<BlockedThread> waitingConsumers = new ArrayDeque<>();

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.BlockingQueueModule$put, this::putHandler)
            .handler(WenyanSymbol.BlockingQueueModule$take, this::takeHandler)
            .handler(WenyanSymbol.BlockingQueueModule$offer, this::offerHandler)
            .handler(WenyanSymbol.BlockingQueueModule$poll, this::pollHandler)
            .handler(WenyanSymbol.BlockingQueueModule$peek, _ -> queue.isEmpty() ? WenyanNull.NULL : queue.peek())
            .handler(WenyanSymbol.BlockingQueueModule$size, _ -> WenyanValues.of(queue.size()))
            .handler(WenyanSymbol.BlockingQueueModule$clear, _ -> {
                if (queue.isEmpty())
                    return WenyanNull.NULL;

                queue.clear();
                for (int i = 0; i < Math.min(10, waitingProducers.size()); i++) {
                    BlockedThread blockedThread = waitingProducers.poll();
                    blockedThread.awake(queue, getLevel(), this::getBlockPos);
                }

                return WenyanNull.NULL;
            })
            .build();

    private boolean putHandler(IHandleContext context, IArgsRequest request) throws WenyanException {
        IWenyanValue value = extractSingleValueFromRequest(request);
        if (queue.size() >= capacity) {
            // Queue is full, block the producer thread
            waitingProducers.add(new BlockedThread(request.thread(), value,
                    context instanceof BlockRequest.BlockContext bc ? bc.pos() : null));
        } else {
            IWenyanRunner thread = request.thread();
            queue.offer(value);
            thread.getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
            thread.unblock();

            // Wake up a waiting consumer if any
            if (!waitingConsumers.isEmpty()) {
                BlockedThread blockedThread = waitingConsumers.poll();
                blockedThread.awake(queue, getLevel(), this::getBlockPos);
            }
        }
        return true;
    }

    private boolean takeHandler(IHandleContext context, IHandleableRequest request) throws WenyanException {
        if (queue.isEmpty()) {
            waitingConsumers.add(new BlockedThread(request.thread(), null,
                    context instanceof BlockRequest.BlockContext bc ? bc.pos() : null));
        } else {
            IWenyanRunner thread = request.thread();
            IWenyanValue value = queue.poll();
            thread.getCurrentRuntime().pushReturnValue(value);
            thread.unblock();

            // Wake up a waiting producer if any
            if (!waitingProducers.isEmpty()) {
                BlockedThread blockedThread = waitingProducers.poll();
                blockedThread.awake(queue, getLevel(), this::getBlockPos);
            }
        }
        return true;
    }

    private WenyanBoolean offerHandler(IArgsRequest request) throws WenyanException {
        if (queue.size() >= capacity) {
            // Queue is full, return false immediately (non-blocking)
            return WenyanValues.of(false);
        } else {
            // Add element to queue
            queue.offer(extractSingleValueFromRequest(request));

            // Wake up a waiting consumer if any
            if (!waitingConsumers.isEmpty()) {
                BlockedThread blockedThread = waitingConsumers.poll();
                blockedThread.awake(queue, getLevel(), this::getBlockPos);
            }
            return WenyanValues.of(true);
        }
    }

    private IWenyanValue pollHandler(IHandleableRequest request) throws WenyanException {
        if (queue.isEmpty()) {
            // Queue is empty, return null immediately (non-blocking)
            return WenyanNull.NULL;
        } else {
            // Remove and return head element
            IWenyanValue value = queue.poll();

            // Wake up a waiting producer if any
            if (!waitingProducers.isEmpty()) {
                BlockedThread blockedThread = waitingProducers.poll();
                blockedThread.awake(queue, getLevel(), this::getBlockPos);
            }
            return value;
        }
    }

    /**
     * Extracts value from request parameters.
     * This is a simplified extraction - adjust based on actual request structure.
     */
    private IWenyanValue extractSingleValueFromRequest(IArgsRequest request) throws WenyanException {
        if (request.args().size() != 1) throw new WenyanException(ArgsNumWrong.string(1, request.args().size()));
        return request.args().getFirst();
    }

    public BlockingQueueModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get(), pos, blockState);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        tickCommunicate();
    }

    private record BlockedThread(IWenyanRunner thread, @Nullable IWenyanValue value,
                                 @Nullable BlockPos pos) {
        @Override
        public boolean equals(Object o) {
            return false;
        }

        private void awake(Queue<IWenyanValue> queue, @Nullable Level level, Supplier<BlockPos> blockPos) throws WenyanUnreachedException {
            if (pos != null) {
                if (level instanceof ServerLevel sl) {
                    PacketDistributor.sendToPlayersTrackingChunk(sl,
                            ChunkPos.containing(pos),
                            new CommunicationLocationPacket(blockPos.get(), pos.subtract(blockPos.get()))
                    );
                }
            }

            if (value != null) {
                queue.offer(value);
                thread.getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
            } else {
                IWenyanValue value1 = queue.poll();
                thread.getCurrentRuntime().pushReturnValue(value1);
            }

            thread.unblock();
        }
    }
}