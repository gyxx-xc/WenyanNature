package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BitModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "bit_module_block";


    public static final MapCodec<BitModuleBlock> CODEC = simpleCodec(BitModuleBlock::new);

    public BitModuleBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected @NotNull MapCodec<BitModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.BIT_MODULE_ENTITY.get();
    }
}
