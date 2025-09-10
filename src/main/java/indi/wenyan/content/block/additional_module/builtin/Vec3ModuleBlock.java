package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.AbstractModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class Vec3ModuleBlock extends AbstractFuluBlock implements AbstractModuleBlock {
    public static final String ID = "vec3_module_block";

    @Override
    protected @NotNull BlockEntityType<?> getType() {
        return Registration.VEC3_MODULE_ENTITY.get();
    }
}

