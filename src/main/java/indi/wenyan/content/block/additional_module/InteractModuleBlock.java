package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class InteractModuleBlock extends AbstractModuleBlock {
    public static final String ID = "interact_module_block";

    @Override
    protected @NotNull BlockEntityType<?> getType() {
        return Registration.INTERACT_MODULE_ENTITY.get();
    }
}
