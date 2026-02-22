package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "math_module_block";

    public static final MapCodec<MathModuleBlock> CODEC = simpleCodec(MathModuleBlock::new);

    public MathModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public @NotNull MapCodec<MathModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<MathModuleEntity> getType() {
        return WenyanBlocks.MATH_MODULE_ENTITY.get();
    }
}
