package indi.wenyan.content.block.additional_module;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BlockModuleBlock extends AbstractFuluBlock implements IModulerBlock {
    public static final String ID = "block_module_block";

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.BLOCK_MODULE_ENTITY.get();
    }
}
