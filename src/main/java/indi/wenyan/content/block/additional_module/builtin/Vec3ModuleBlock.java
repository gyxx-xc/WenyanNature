package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class Vec3ModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "vec3_module_block";
    
    public static final MapCodec<Vec3ModuleBlock> CODEC = simpleCodec(ignore -> new Vec3ModuleBlock());

    public Vec3ModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<Vec3ModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.VEC3_MODULE_ENTITY.get();
    }
}
