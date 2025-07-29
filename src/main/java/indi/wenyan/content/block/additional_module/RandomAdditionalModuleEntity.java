package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class RandomAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「易經」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .intFunction("「占數」", (args) -> {
                var random = Objects.requireNonNull(Minecraft.getInstance().level).getRandom();
                return switch (args.size()) {
                    case 0 -> random.nextInt();
                    case 1 -> random.nextInt(args.getFirst());
                    case 2 -> random.nextInt(args.get(0), args.get(1));
                    default -> throw new WenyanException(""); // TODO
                };
            })
            .doubleFunction("「占分」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextDouble())
            .doubleFunction("「占偏」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().triangle(args.getFirst(), args.get(1)))
            .function("「占爻」", (self, args) -> new WenyanBoolean(Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextBoolean()))
            .build();

    public RandomAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RANDOM_MODULE_ENTITY.get(), pos, blockState);
    }
}
