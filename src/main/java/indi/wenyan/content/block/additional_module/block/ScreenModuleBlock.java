package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ScreenModuleBlock extends Block implements IModuleBlock {
    public static final String ID = "screen_block_module_block";

    public ScreenModuleBlock() {
        super(Properties.of());
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return Registration.SCREEN_MODULE_BLOCK_ENTITY.get();
    }
}
