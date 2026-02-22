package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class StringModuleBlock extends AbstractFuluBlock implements IModuleBlock {
    public static final String ID = "string_module_block";
    
    public static final MapCodec<StringModuleBlock> CODEC = simpleCodec(StringModuleBlock::new);

    public StringModuleBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected @NotNull MapCodec<StringModuleBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public BlockEntityType<?> getType() {
        return WenyanBlocks.STRING_MODULE_ENTITY.get();
    }
}

