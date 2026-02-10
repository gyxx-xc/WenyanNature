package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "math_module_block";

    @Override
    @NotNull
    public BlockEntityType<MathModuleEntity> getType() {
        return Registration.MATH_MODULE_ENTITY.get();
    }
}
