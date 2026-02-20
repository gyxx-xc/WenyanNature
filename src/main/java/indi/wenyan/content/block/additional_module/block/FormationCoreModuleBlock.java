package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class FormationCoreModuleBlock extends Block implements IModuleBlock {
    public static final String ID = "formation_core_module_block";

    public FormationCoreModuleBlock() {
        super(Properties.of());
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return Registration.FORMATION_CORE_MODULE_ENTITY.get();
    }
}
