package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BitAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「位經」";

    @Getter
    private final WenyanRuntime execPackage = WenyanPackageBuilder.create()
            .intFunction("「左移」", args -> args.getFirst()<<args.get(1))
            .intFunction("「右移」", args -> args.getFirst()>>args.get(1))
            .intFunction("「補零右移」", args -> args.getFirst()>>>args.get(1))
            .intFunction("「位與」", args -> args.getFirst()&args.get(1))
            .intFunction("「位或」", args -> args.getFirst()|args.get(1))
            .intFunction("「異或」", args -> args.getFirst()^args.get(1))
            .intFunction("「與非」", args -> ~(args.getFirst()&args.get(1)))
            .intFunction("「位變」", args -> ~args.getFirst())
            .build();

    public BitAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.BIT_MODULE_ENTITY.get(), pos, blockState);
    }
}
