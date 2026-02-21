package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class EntityModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "entity_module_block";
    
    public static final MapCodec<EntityModuleBlock> CODEC = simpleCodec(ignore -> new EntityModuleBlock());

    public EntityModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<EntityModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.ENTITY_MODULE_ENTITY.get();
    }
}

