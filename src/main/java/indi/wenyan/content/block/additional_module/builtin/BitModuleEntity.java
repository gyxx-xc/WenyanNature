package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BitModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("BitModule");

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .intFunction(WenyanSymbol.var("BitModule.leftShift"), args -> args.getFirst() << args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.rightShift"), args -> args.getFirst() >> args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.zeroFillRightShift"), args -> args.getFirst() >>> args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.bitAnd"), args -> args.getFirst() & args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.bitOr"), args -> args.getFirst() | args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.bitXor"), args -> args.getFirst() ^ args.get(1))
            .intFunction(WenyanSymbol.var("BitModule.bitNand"), args -> ~(args.getFirst() & args.get(1)))
            .intFunction(WenyanSymbol.var("BitModule.bitNot"), args -> ~args.getFirst())
            .build();

    public BitModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.BIT_MODULE_ENTITY.get(), pos, blockState);
    }
}
