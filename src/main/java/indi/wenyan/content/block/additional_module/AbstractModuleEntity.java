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

/**
 * Base class for module entities that can execute Wenyan code.
 * Implements IWenyanDevice for integration with the Wenyan interpreter.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractModuleEntity extends DataBlockEntity implements IWenyanDevice {
    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    @Nullable
    private String packageName;

    protected AbstractModuleEntity(
            BlockEntityType<? extends AbstractModuleEntity> type,
            BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * Gets the default package name for this module entity.
     *
     * @return the base package name
     */
    public abstract String getBasePackageName();

    /**
     * Gets the package that provides execution capabilities for this entity.
     *
     * @return the execution package
     */
    public abstract WenyanPackage getExecPackage();

    @Override
    public Vec3 getPosition() {
        return getBlockPos().getCenter();
    }

    @Override
    public String getPackageName() {
        return Objects.requireNonNullElseGet(packageName, this::getBasePackageName);
    }

    /**
     * Sets the package name for this module entity.
     *
     * @param packageName the new package name, or null to use the base package name
     */
    public void setPackageName(@Nullable String packageName) {
        this.packageName = packageName;
        setChanged();
    }

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

    /**
     * An abstract call handler that uses this module entity as the executor.
     */
    protected abstract class ThisCallHandler implements IExecCallHandler {
        @Override
        public Optional<IWenyanDevice> getExecutor() {
            if (isRemoved())
                return Optional.empty();
            return Optional.of(AbstractModuleEntity.this);
        }
    }

    /**
     * Called every tick to handle execution requests.
     */
    public void tick() {
        handle();
    }
}
