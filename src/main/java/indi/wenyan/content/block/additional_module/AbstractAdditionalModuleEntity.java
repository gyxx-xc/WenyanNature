package indi.wenyan.content.block.additional_module;

import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.IWenyanDevice;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAdditionalModuleEntity extends DataBlockEntity implements IWenyanDevice {
    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    @Nullable
    private String packageName;

    public void setPackageName(@Nullable String packageName) {
        this.packageName = packageName;
        setChanged();
    }

    protected AbstractAdditionalModuleEntity(
            BlockEntityType<? extends AbstractAdditionalModuleEntity> type,
            BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public Vec3 getPosition() {
        return getBlockPos().getCenter();
    }

    @Override
    public String getPackageName() {
        return Objects.requireNonNullElseGet(packageName, this::getBasePackageName);
    }

    public abstract String getBasePackageName();

    public abstract WenyanPackage getExecPackage();

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (packageName != null)
            tag.putString("packageName", packageName);
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("packageName"))
            packageName = tag.getString("packageName");
    }

    abstract class ThisCallHandler implements IExecCallHandler {
        @Override
        public Optional<IWenyanDevice> getExecutor() {
            if (isRemoved())
                return Optional.empty();
            return Optional.of(AbstractAdditionalModuleEntity.this);
        }
    }
}
