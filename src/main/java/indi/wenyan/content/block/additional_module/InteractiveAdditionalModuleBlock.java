package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class InteractiveAdditionalModuleBlock extends AbstractAdditionalModuleBlock {
    public static final String ID = "interactive_module_block";

    @Override
    protected @NotNull BlockEntityType<?> getType() {
        return Registration.INTERACTIVE_MODULE_ENTITY.get();
    }
}
