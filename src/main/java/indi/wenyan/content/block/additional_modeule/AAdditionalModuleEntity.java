package indi.wenyan.content.block.additional_modeule;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AAdditionalModuleEntity extends BlockEntity implements IWenyanExecutor {
    @Getter
    private final ExecQueue execQueue = new ExecQueue();
    @Getter
    private final String packageName = "「「a」」";
    @Getter
    private final WenyanRuntime execPackage = WenyanPackageBuilder.create()
            .function("「a」", new IExecCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    assert level != null;
                    Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    e.moveTo(getBlockPos().getCenter());
                    level.addFreshEntity(e);
                    return WenyanNull.NULL;
                }

                @Override
                public Optional<IWenyanExecutor> getExecutor() {
                    return Optional.of(AAdditionalModuleEntity.this);
                }
            })
            .build();

    public AAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.ADDITIONAL_MODULE_ENTITY.get(), pos, blockState);
    }
}
