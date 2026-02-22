package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class CommunicateModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "communicate_module_block";
    
    public static final MapCodec<CommunicateModuleBlock> CODEC = simpleCodec(CommunicateModuleBlock::new);

    public CommunicateModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<CommunicateModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.COMMUNICATE_MODULE_ENTITY.get();
    }
}
