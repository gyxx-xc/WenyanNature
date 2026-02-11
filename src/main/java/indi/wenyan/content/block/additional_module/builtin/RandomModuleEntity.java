package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.RawHandlerPackage;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RandomModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("RandomModule");

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .intFunction(WenyanSymbol.var("RandomModule.nextInt"), args -> {
                        assert getLevel() != null;
                        var random = getLevel().getRandom();
                        return switch (args.size()) {
                            case 0 -> random.nextInt();
                            case 1 -> random.nextInt(args.getFirst());
                            case 2 -> random.nextInt(args.get(0), args.get(1));
                            default -> throw new WenyanException(""); // TODO
                        };
                    })
                    .doubleFunction(WenyanSymbol.var("RandomModule.nextDouble"), args -> {
                        assert getLevel() != null;
                        return getLevel().getRandom().nextDouble();
                    })
                    .doubleFunction(WenyanSymbol.var("RandomModule.nextTriangle"), args -> {
                        assert getLevel() != null;
                        return getLevel().getRandom().triangle(args.getFirst(), args.get(1));
                    })
                    .function(WenyanSymbol.var("RandomModule.nextBoolean"), (IWenyanValue self, List<IWenyanValue> args) -> {
                        assert getLevel() != null;
                        return WenyanValues.of(getLevel().getRandom().nextBoolean());
                    }))
            .build();

    public RandomModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RANDOM_MODULE_ENTITY.get(), pos, blockState);
    }
}
