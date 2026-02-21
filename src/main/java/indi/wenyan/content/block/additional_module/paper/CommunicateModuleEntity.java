package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.warper.WenyanList;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;

public class CommunicateModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("CommunicateModule");

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .constant(WenyanSymbol.var("CommunicateModule.self"), new WenyanList(Collections.nCopies(8,
                    new WenyanLeftValue(WenyanNull.NULL)))))
            .build();

    public CommunicateModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.COMMUNICATE_MODULE_ENTITY.get(), pos, blockState);
    }
}
