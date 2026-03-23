package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

/**
 * Block for the blocking queue module.
 * Provides synchronization capabilities for multi-threaded execution.
 */
public class BlockingQueueModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "blocking_queue_module_block";
    
    public static final MapCodec<BlockingQueueModuleBlock> CODEC = simpleCodec(BlockingQueueModuleBlock::new);

    public BlockingQueueModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<BlockingQueueModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get();
    }
}
