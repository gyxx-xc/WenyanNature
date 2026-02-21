package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class RandomModuleBlock extends AbstractFuluBlock implements IModuleBlock {

    public static final String ID = "random_module_block";
    
    public static final MapCodec<RandomModuleBlock> CODEC = simpleCodec(ignore -> new RandomModuleBlock());

    public RandomModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public @NotNull MapCodec<RandomModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.RANDOM_MODULE_ENTITY.get();
    }
}
