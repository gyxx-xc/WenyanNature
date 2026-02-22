package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BlockModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "block_module_block";
    
    public static final MapCodec<BlockModuleBlock> CODEC = simpleCodec(BlockModuleBlock::new);

    public BlockModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<BlockModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.BLOCK_MODULE_ENTITY.get();
    }
}
