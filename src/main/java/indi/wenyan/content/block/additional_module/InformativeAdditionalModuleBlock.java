package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class InformativeAdditionalModuleBlock extends AbstractAdditionalModuleBlock {
    public static final String ID = "informative_module_block";

    @Override
    @NotNull
    BlockEntityType<?> getType() {
        return Registration.INFORMATIVE_MODULE_ENTITY.get();
    }
}
