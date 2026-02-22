package indi.wenyan.content.block.additional_module.paper;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WYRegistration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class ItemModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "item_module_block";
    
    public static final MapCodec<ItemModuleBlock> CODEC = simpleCodec(ignore -> new ItemModuleBlock());

    public ItemModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<ItemModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WYRegistration.ITEM_MODULE_ENTITY.get();
    }
}
