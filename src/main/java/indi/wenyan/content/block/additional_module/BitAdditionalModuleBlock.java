package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BitAdditionalModuleBlock extends AbstractAdditionalModuleBlock{
    public static final String ID = "bit_module_block";

    @Override
    protected @NotNull BlockEntityType<?> getType() {
        return Registration.BIT_MODULE_ENTITY.get();
    }
}
