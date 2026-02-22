package indi.wenyan.content.block.additional_module.paper;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "explosion_module_block";
    public static final MapCodec<ExplosionModuleBlock> CODEC = simpleCodec(ExplosionModuleBlock::new);

    public ExplosionModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ExplosionModuleBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<?> getType() {
        return WenyanBlocks.EXPLOSION_MODULE_ENTITY.get();
    }
}
