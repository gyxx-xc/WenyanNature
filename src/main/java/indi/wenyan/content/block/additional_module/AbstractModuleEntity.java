package indi.wenyan.content.block.additional_module;

import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.interpreter.exec_interface.IWenyanBlockDevice;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Base class for module entities that can execute Wenyan code.
 * Implements IWenyanDevice for integration with the Wenyan interpreter.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractModuleEntity extends DataBlockEntity implements IWenyanBlockDevice {
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

    @Override
    public BlockState blockState() {
        return getBlockState();
    }

    @Override
    public BlockPos blockPos() {
        return getBlockPos();
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
     * Called every tick to handle execution requests.
     */
    public void tick() {}
}
