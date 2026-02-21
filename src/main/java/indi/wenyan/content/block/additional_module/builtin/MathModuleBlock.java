package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "math_module_block";

    public static final MapCodec<MathModuleBlock> CODEC = simpleCodec(ignore -> new MathModuleBlock());

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
        return Registration.MATH_MODULE_ENTITY.get();
    }
}
