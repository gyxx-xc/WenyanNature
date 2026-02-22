package indi.wenyan.content.block.additional_module;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public static final String PACKAGE_NAME_ID = "packageName";

    @Nullable
    private String packageName;

    protected AbstractModuleEntity(
            BlockEntityType<?> type,
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
    // make the override explicitly to make it clear that this method is overridden
    public boolean isRemoved() {
        return super.isRemoved();
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
    protected void saveData(ValueOutput output) {
        if (packageName != null)
            output.putString(PACKAGE_NAME_ID, packageName);
    }

    @Override
    protected void loadData(ValueInput input) {
        input.getString(PACKAGE_NAME_ID).ifPresent(this::setPackageName);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        Component name = components.get(DataComponents.CUSTOM_NAME);
        if (name != null)
            setPackageName(Component.translatable("code.wenyan_programming.bracket", name).getString());
    }

    /**
     * Called every tick to handle execution requests.
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
    }
}
