package indi.wenyan.content.block.additional_module.paper;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.IArgsRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Entity for the blocking queue module.
 * Provides thread-safe queue operations for synchronization.
 * Uses manual blocking/unblocking mechanism similar to LockModuleEntity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockingQueueModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("BlockingQueueModule");

    private final Queue<IWenyanValue> queue = new ArrayDeque<>();
    private final int capacity = 10; // Default capacity

    private final Queue<ThreadWithValue> waitingProducers = new ArrayDeque<>();
    private final Queue<WenyanRunner> waitingConsumers = new ArrayDeque<>();

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("BlockingQueueModule.put"), this::putHandler)
            .handler(WenyanSymbol.var("BlockingQueueModule.take"), this::takeHandler)
            .handler(WenyanSymbol.var("BlockingQueueModule.offer"), this::offerHandler)
            .handler(WenyanSymbol.var("BlockingQueueModule.poll"), this::pollHandler)
            .handler(WenyanSymbol.var("BlockingQueueModule.peek"), _ -> queue.isEmpty() ? WenyanNull.NULL : queue.peek())
            .handler(WenyanSymbol.var("BlockingQueueModule.size"), _ -> WenyanValues.of(queue.size()))
            .handler(WenyanSymbol.var("BlockingQueueModule.clear"), _ -> {
                if (queue.isEmpty())
                    return WenyanNull.NULL;

                queue.clear();
                for (int i = 0; i < Math.min(10, waitingProducers.size()); i++) {
                    ThreadWithValue threadWithValue = waitingProducers.poll();
                    offer(threadWithValue.thread, threadWithValue.value);
                }

                return WenyanNull.NULL;
            })
            .build();

    private void offer(WenyanRunner thread, IWenyanValue value) throws WenyanUnreachedException {
        queue.offer(value);
        thread.getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
        thread.unblock();
    }

    private void poll(WenyanRunner thread) throws WenyanUnreachedException {
        IWenyanValue value = queue.poll();
        thread.getCurrentRuntime().pushReturnValue(value);
        thread.unblock();
    }

    private boolean putHandler(IHandleContext context, IArgsRequest request) throws WenyanException {
        IWenyanValue value = extractSingleValueFromRequest(request);
        if (value == null) {
            throw new WenyanException("Invalid value for BlockingQueueModule.put");
        }

        if (queue.size() >= capacity) {
            // Queue is full, block the producer thread
            waitingProducers.add(new ThreadWithValue(request.thread(), value));
        } else {
            offer(request.thread(), value);

            // Wake up a waiting consumer if any
            if (!waitingConsumers.isEmpty()) {
                poll(waitingConsumers.poll());
            }
        }
        return true;
    }

    private boolean takeHandler(IHandleContext context, IHandleableRequest request) throws WenyanException {
        if (queue.isEmpty()) {
            waitingConsumers.add(request.thread());
        } else {
            poll(request.thread());

            // Wake up a waiting producer if any
            if (!waitingProducers.isEmpty()) {
                ThreadWithValue threadWithValue = waitingProducers.poll();
                offer(threadWithValue.thread, threadWithValue.value);
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
                poll(waitingConsumers.poll());
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
                ThreadWithValue threadWithValue = waitingProducers.poll();
                offer(threadWithValue.thread, threadWithValue.value);
            }
            return value;
        }
    }

    /**
     * Extracts value from request parameters.
     * This is a simplified extraction - adjust based on actual request structure.
     */
    private @Nullable IWenyanValue extractSingleValueFromRequest(IArgsRequest request) {
        if (request.args().size() != 1) return null;
        return request.args().getFirst();
    }

    public BlockingQueueModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get(), pos, blockState);
    }

    private record ThreadWithValue(WenyanRunner thread, IWenyanValue value) {
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
}