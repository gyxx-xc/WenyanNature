package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InteractiveAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「im」";

    // interactive, inventory
    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .build();

    public InteractiveAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INTERACTIVE_MODULE_ENTITY.get(), pos, blockState);
    }

}