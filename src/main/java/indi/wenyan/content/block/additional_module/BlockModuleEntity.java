package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「塊」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .build();

    public BlockModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_MODULE_ENTITY.get(), pos, blockState);
    }
}
