package indi.wenyan.content.block.additional_module.paper.piston;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

public class PistonModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "piston_module_block";

    public static final MapCodec<PistonModuleBlock> CODEC = simpleCodec(PistonModuleBlock::new);

    public PistonModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull BlockEntityType<?> getType() {
        return WenyanBlocks.PISTON_MODULE_ENTITY.get();
    }
}
