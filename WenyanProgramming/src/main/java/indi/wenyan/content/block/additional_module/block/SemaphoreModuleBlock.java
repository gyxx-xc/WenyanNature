package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class SemaphoreModuleBlock extends Block implements IModuleBlock {
    public static final String ID = "semaphore_module_block";

    public SemaphoreModuleBlock() {
        super(Properties.of());
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.SEMAPHORE_MODULE_ENTITY.get();
    }
}
