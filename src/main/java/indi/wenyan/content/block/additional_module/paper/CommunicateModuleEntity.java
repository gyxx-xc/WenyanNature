package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.values.WenyanLeftValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.warper.WenyanArrayList;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;

public class CommunicateModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「通」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .constant("「我」", new WenyanArrayList(Collections.nCopies(8,
                    new WenyanLeftValue(WenyanNull.NULL))))
            .build();

    public CommunicateModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.COMMUNICATE_MODULE_ENTITY.get(), pos, blockState);
    }
}
