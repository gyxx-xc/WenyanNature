package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class RandomModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("RandomModule");

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .intFunction(WenyanSymbol.var("RandomModule.nextInt"), (args) -> {
                var random = Objects.requireNonNull(Minecraft.getInstance().level).getRandom();
                return switch (args.size()) {
                    case 0 -> random.nextInt();
                    case 1 -> random.nextInt(args.getFirst());
                    case 2 -> random.nextInt(args.get(0), args.get(1));
                    default -> throw new WenyanException(""); // TODO
                };
            })
            .doubleFunction(WenyanSymbol.var("RandomModule.nextDouble"), args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextDouble())
            .doubleFunction(WenyanSymbol.var("RandomModule.nextTriangle"), args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().triangle(args.getFirst(), args.get(1)))
            .function(WenyanSymbol.var("RandomModule.nextBoolean"), (self, args) -> WenyanValues.of(Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextBoolean()))
            .build();

    public RandomModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RANDOM_MODULE_ENTITY.get(), pos, blockState);
    }
}
