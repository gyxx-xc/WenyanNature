package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.AbstractModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock implements AbstractModuleBlock {
    public static final String ID = "math_module_block";

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.MATH_MODULE_ENTITY.get();
    }
}
