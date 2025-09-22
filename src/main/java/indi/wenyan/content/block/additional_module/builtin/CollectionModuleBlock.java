package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModulerBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class CollectionModuleBlock extends AbstractFuluBlock implements IModulerBlock {
    public static final String ID = "collection_module_block";

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return Registration.COLLECTION_MODULE_ENTITY.get();
    }
}
