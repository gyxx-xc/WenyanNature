package indi.wenyan.content.entity;

import indi.wenyan.content.block.runner.LazyProgram;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ThrowRunnerEntity extends ThrowableItemProjectile
        implements IWenyanPlatform {
    public static final String ID = "throw_runner_entity";
    private static final int LIFE_TIME = 100;

    @Getter private String platformName;
    @Getter private final ExecQueue execQueue = new ExecQueue(this);

    @Nullable
    private final Player player;
    private final RunnerTier tier;
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    private final LazyProgram<IWenyanProgram<WenyanProgramImpl.PCB>> lazyProgram =
            new LazyProgram<>(() -> new WenyanProgramImpl(this));

    private int life = 0;

    public ThrowRunnerEntity(EntityType<ThrowRunnerEntity> entityType, Level level) {
        super(entityType, level);
        this.tier = RunnerTier.RUNNER_0;
        player = null;
    }

    public ThrowRunnerEntity(Level level, LivingEntity owner, @NotNull ItemStack itemStack, @NotNull RunnerTier tier) {
        super(WyRegistration.THROW_RUNNER_ENTITY.get(), owner, level, itemStack);
        setRemainingFireTicks(1);
        this.tier = tier;
        if (!level.isClientSide()) {
            if (owner instanceof Player p)
                this.player = p;
            else
                this.player = null;
            var code = itemStack.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
            if (code != null) {
                platformName = code.getPlatformName();
                try {
                    lazyProgram.create().create(WenyanRunner.of(WenyanFrame.ofCode(code.getCode()), this.initEnvironment()));
                } catch (WenyanException | WenyanCompileException e) {
                    handleError(e.getMessage());
                    // add will show this message and kill itself at tick
                }
            } else {
                platformName = "";
                // and discard in tick
            }
        } else {
            this.player = null;
            platformName = "";
        }
    }

    @Override
    public void handleError(String error) {
        errors.add(error);
    }

    @Override
    public void tick() {
        if (life++ > LIFE_TIME)
            discard();

        if (!level().isClientSide()) {
            // need ensure error only increase size in other thread
            if (player != null && !errors.isEmpty()) {
                player.sendSystemMessage(Component.literal(errors.removeFirst()).withStyle(ChatFormatting.RED));
                errors.clear();
            }

            lazyProgram.ifCreated()
                    .filter(IWenyanProgram::isRunning)
                    .ifPresentOrElse(program -> {
                        program.step(tier.getStepSpeed());
                        handle(getContext());
                    }, this::discard);
        }

        updateRotation();

        Vec3 movement = this.getDeltaMovement();
        setPos(position().add(movement));
        setDeltaMovement(movement.scale(0.85));

        super.tick();
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0);
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState blockstate) {
        if (!blockstate.isAir() && !getDeltaMovement().equals(Vec3.ZERO)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(level(), blockPosition());
            if (!voxelshape.isEmpty()) {
                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockPosition()).contains(position())) {
                        setDeltaMovement(Vec3.ZERO);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        lazyProgram.ifCreated().ifPresent(IWenyanProgram::stop);
        super.remove(reason);
    }

    @Override
    public void setRemainingFireTicks(int ignore) {
        // HACK: make it contious on fire
        super.setRemainingFireTicks(1);
    }

//    @WenyanThreading
//    abstract class ThisCallHandler implements ISimpleExecCallHandler {
//        @Override
//        public @NotNull Optional<IWenyanDevice> getExecutor() {
//            if (isRemoved())
//                return Optional.empty();
//            return Optional.of(HandRunnerEntity.this);
//        }
//    }

    private @NonNull ThrowEntityContext getContext() {
        return new ThrowEntityContext();
    }

    public static class ThrowEntityContext implements IHandleContext {
    }
}
