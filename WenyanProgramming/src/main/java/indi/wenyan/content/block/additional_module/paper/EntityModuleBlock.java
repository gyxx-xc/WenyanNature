package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class EntityModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "entity_module_block";

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.ENTITY_MODULE_ENTITY.get();
    }
}

