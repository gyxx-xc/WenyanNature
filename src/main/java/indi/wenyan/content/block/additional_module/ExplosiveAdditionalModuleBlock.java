package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosiveAdditionalModuleBlock extends AbstractAdditionalModuleBlock {
    public static final String ID = "explosive_module_block";

    @Override
    BlockEntityType<?> getType() {
        return Registration.EXPLOSIVE_MODULE_ENTITY.get();
    }

    public ExplosiveAdditionalModuleBlock() {
        super(PROPERTIES);
    }

}
