package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosiveAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「em」";
    @Getter
    private final WenyanRuntime execPackage = WenyanPackageBuilder.create()
            .function("「l」", new IThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    assert level != null;
                    Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    e.moveTo(getBlockPos().getCenter());
                    level.addFreshEntity(e);
                    return WenyanNull.NULL;
                }
            })
            .build();

    public ExplosiveAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.EXPLOSIVE_MODULE_ENTITY.get(), pos, blockState);
    }
}
