package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModulerBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionModuleBlock extends AbstractFuluBlock implements IModulerBlock {
    public static final String ID = "explosion_module_block";

    @Override
    public BlockEntityType<?> getType() {
        return Registration.EXPLOSION_MODULE_ENTITY.get();
    }
}
