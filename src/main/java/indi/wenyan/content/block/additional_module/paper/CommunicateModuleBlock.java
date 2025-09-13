package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModulerBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class CommunicateModuleBlock extends AbstractFuluBlock implements IModulerBlock {
    public static final String ID = "communicate_module_block";

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.COMMUNICATE_MODULE_ENTITY.get();
    }
}
