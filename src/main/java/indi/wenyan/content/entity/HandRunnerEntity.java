package indi.wenyan.content.entity;

import com.google.common.collect.Lists;
import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.IWenyanDevice;
import indi.wenyan.interpreter.utils.IWenyanPlatform;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class HandRunnerEntity extends Projectile implements IWenyanPlatform, IWenyanDevice {
    public static final String ID_1 = "hand_runner";
    // String constants for registry names and entity IDs
    public static final String ID_0 = "hand_runner_0";
    public static final String ID_2 = "hand_runner_2";
    public static final String ID_3 = "hand_runner_3";
    public WenyanProgram program;
    public boolean hasRun = false;
    public int speed;

    private final ExecQueue execQueue = new ExecQueue();

    public HandRunnerEntity(EntityType<HandRunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HandRunnerEntity(@NotNull Player holder, String code, int level) {
        super(Registration.HAND_RUNNER_ENTITY.get(), holder.level());
        speed = (int) StrictMath.pow(10, level);
        program = new WenyanProgram(code, holder, this);

        Vec3 lookDirection = Vec3.directionFromRotation(holder.getXRot(), holder.getYRot()).normalize().scale(0.5);
        moveTo(holder.getEyePosition().add(lookDirection.x, -0.5, lookDirection.z));
        shoot(lookDirection.x, lookDirection.y+0.5, lookDirection.z, 0.1F, 10.0F);
        addDeltaMovement(holder.getDeltaMovement());
    }

    @Override
    public WenyanPackage getExecPackage() {
        return WenyanPackageBuilder.create()
                .function("a", new ThisCallHandler() {
                    @Override
                    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                        Vec3 dir = new Vec3(
                                Math.max(-10, Math.min(10, context.args().get(0).as(WenyanDouble.TYPE).value())),
                                Math.max(-10, Math.min(10, context.args().get(1).as(WenyanDouble.TYPE).value())),
                                Math.max(-10, Math.min(10, context.args().get(2).as(WenyanDouble.TYPE).value())));

                        BulletEntity bullet = new BulletEntity(level(), getPosition(0),
                                dir, Math.max(1,
                                Math.min(20, context.args().get(3).as(WenyanDouble.TYPE).value())) / 10,
                                Math.max(1, Math.min(200, context.args().get(4).as(WenyanInteger.TYPE).value())),
                                context.holder());
                        level().addFreshEntity(bullet);
                        return WenyanNull.NULL;
                    }
                })
                .function("b", new ThisCallHandler() {
                    @Override
                    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                        List<Double> newArgs = Lists.newArrayList();
                        newArgs.add(Math.max(-20, Math.min(20, context.args().get(0).as(WenyanDouble.TYPE).value())));
                        newArgs.add(Math.max(-20, Math.min(20, context.args().get(1).as(WenyanDouble.TYPE).value())));
                        newArgs.add(Math.max(-20, Math.min(20, context.args().get(2).as(WenyanDouble.TYPE).value())));
                        setDeltaMovement(new Vec3(newArgs.get(0) / 10,
                                newArgs.get(1) / 10, newArgs.get(2) / 10));
                        return WenyanNull.NULL;

                    }
                })
                .function("「爆」", new ThisCallHandler() {
                    @Override
                    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                        level().explode(HandRunnerEntity.this, getX(), getY(), getZ(),
                                (float) Math.max(1, Math.min(20,
                                        context.args().getFirst().as(WenyanDouble.TYPE).value())),
                                Level.ExplosionInteraction.MOB);
                        return WenyanNull.NULL;
                    }
                })
                .object("「方位」", WenyanVec3.OBJECT_TYPE)
                .build();
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public void accept(JavacallContext context) {
        context.handler().getExecutor().ifPresent((device) -> device.receive(context));
    }

    @Override
    public void initEnvironment(WenyanRuntime baseEnvironment) {
        baseEnvironment.importEnvironment(getExecPackage());
    }

    @Override
    public ExecQueue getExecQueue() {
        return execQueue;
    }

    @Override
    public Vec3 getPosition() {
        return getPosition(0);
    }

    @Override
    public void tick() {
        if (!hasRun) {
            if (getDeltaMovement().length() < 0.01) {
                setDeltaMovement(Vec3.ZERO);
                if (!level().isClientSide())
                    program.createThread();
                hasRun = true;
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.5));
            }
        }
        if (!level().isClientSide() && hasRun) {
            if (program == null || !program.isRunning()) {
                discard();
                return;
            }
            execQueue.handle();
            program.step(speed);
        }
        checkInsideBlocks();
        updateRotation();
        setPos(position().add(getDeltaMovement()));

        super.tick();
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (program != null && program.isRunning())
            program.stop();
        super.remove(reason);
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState blockstate) {
        if (!blockstate.isAir()) {
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    public boolean ignoreExplosion(@NotNull Explosion explosion) {
        return true;
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
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        hasRun = true;
        super.readAdditionalSaveData(compound);
    }

    abstract class ThisCallHandler implements IExecCallHandler {
        @Override
        public Optional<IWenyanDevice> getExecutor() {
            if (isRemoved())
                return Optional.empty();
            return Optional.of(HandRunnerEntity.this);
        }
    }

}
