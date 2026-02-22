package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class CollectionModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "collection_module_block";

    public static final MapCodec<CollectionModuleBlock> CODEC = simpleCodec(CollectionModuleBlock::new);

    public CollectionModuleBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected @NotNull MapCodec<CollectionModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.COLLECTION_MODULE_ENTITY.get();
    }
}
