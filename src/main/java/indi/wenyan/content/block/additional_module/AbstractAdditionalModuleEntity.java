package indi.wenyan.content.block.additional_module;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.IWenyanDevice;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAdditionalModuleEntity extends BlockEntity implements IWenyanDevice {
    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    public AbstractAdditionalModuleEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Vec3 getPosition() {
        return getBlockPos().getCenter();
    }

    public abstract String getPackageName();

    public abstract WenyanPackage getExecPackage();

    abstract class ThisCallHandler implements IExecCallHandler {
        @Override
        public Optional<IWenyanDevice> getExecutor() {
            if (isRemoved())
                return Optional.empty();
            return Optional.of(AbstractAdditionalModuleEntity.this);
        }
    }
}
