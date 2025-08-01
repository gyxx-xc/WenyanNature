package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractModuleBlock {
    public static final String ID = "math_module_block";

    @Override
    protected @NotNull BlockEntityType<?> getType() {
        return Registration.MATH_MODULE_ENTITY.get();
    }
}
