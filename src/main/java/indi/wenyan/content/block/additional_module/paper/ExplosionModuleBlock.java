package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "explosion_module_block";
    
    public static final MapCodec<ExplosionModuleBlock> CODEC = simpleCodec(ignore -> new ExplosionModuleBlock());
    
    @Override
    protected @NotNull MapCodec<ExplosionModuleBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<?> getType() {
        return Registration.EXPLOSION_MODULE_ENTITY.get();
    }
}
