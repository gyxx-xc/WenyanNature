package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionModuleBlock extends AbstractModuleBlock {
    public static final String ID = "explosion_module_block";

    @Override
    protected BlockEntityType<?> getType() {
        return Registration.EXPLOSION_MODULE_ENTITY.get();
    }
}
