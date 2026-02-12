package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BlockModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "block_module_block";
    
    public static final MapCodec<BlockModuleBlock> CODEC = simpleCodec(ignore -> new BlockModuleBlock());
    
    @Override
    protected @NotNull MapCodec<BlockModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.BLOCK_MODULE_ENTITY.get();
    }
}
