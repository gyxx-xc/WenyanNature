package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.values.WenyanLeftValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.primitive.WenyanList;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Stream;

@Deprecated
public class CommunicateModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.CommunicateModule;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .constant(WenyanSymbol.CommunicateModule$self, new WenyanList(Stream.generate(() -> WenyanLeftValue.varOf(WenyanNull.NULL))
                            .limit(8).toList())))
            .build();

    public CommunicateModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.COMMUNICATE_MODULE_ENTITY.get(), pos, blockState);
    }
}
