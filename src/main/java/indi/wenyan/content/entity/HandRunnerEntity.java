package indi.wenyan.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ALL")
@Deprecated
public class HandRunnerEntity extends Projectile
//        implements IWenyanPlatform, IWenyanDevice
{
//    public WenyanProgram program;
//    public boolean hasRun = false;
//    public int speed;
//
//    @Getter
//    private final ExecQueue execQueue = new ExecQueue();

    public HandRunnerEntity(EntityType<HandRunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

//    public HandRunnerEntity(@NotNull Player holder, String code, int level) {
//        super(Registration.HAND_RUNNER_ENTITY.get(), holder.level());
//        speed = (int) StrictMath.pow(10, level);
//        program = new WenyanProgram(code, holder, this);
//
//        Vec3 lookDirection = Vec3.directionFromRotation(holder.getXRot(), holder.getYRot()).normalize().scale(0.5);
//        moveTo(holder.getEyePosition().add(lookDirection.x, -0.5, lookDirection.z));
//        shoot(lookDirection.x, lookDirection.y+0.5, lookDirection.z, 0.1F, 10.0F);
//        addDeltaMovement(holder.getDeltaMovement());
//    }
//
//    @Override
//    public HandlerPackageBuilder.RawHandlerPackage getExecPackage() {
//        return HandlerPackageBuilder.create()
//                .handler("b", (request) -> {
//                    List<Double> newArgs = Lists.newArrayList();
//                    newArgs.add(Math.max(-20, Math.min(20, request.args().get(0).as(WenyanDouble.TYPE).value())));
//                    newArgs.add(Math.max(-20, Math.min(20, request.args().get(1).as(WenyanDouble.TYPE).value())));
//                    newArgs.add(Math.max(-20, Math.min(20, request.args().get(2).as(WenyanDouble.TYPE).value())));
//                    setDeltaMovement(new Vec3(newArgs.get(0) / 10,
//                            newArgs.get(1) / 10, newArgs.get(2) / 10));
//                    return WenyanNull.NULL;
//                })
//                .handler("「爆」", (request) -> {
//                    level().explode(HandRunnerEntity.this, getX(), getY(), getZ(),
//                            (float) Math.max(1, Math.min(20,
//                                    request.args().getFirst().as(WenyanDouble.TYPE).value())),
//                            Level.ExplosionInteraction.MOB);
//                    return WenyanNull.NULL;
//                })
//                .nativeVariables(builder -> builder
//                        .constant("「方位」", WenyanVec3.OBJECT_TYPE))
//                .build();
//    }
//
//    @Override
//    public String getPackageName() {
//        return "";
//    }
//
//    @Override
//    public void changeInitEnvironment(WenyanRuntime baseEnvironment) {
//        baseEnvironment.importPackage(getExecPackage());
//    }
//
//    @Override
//    public void tick() {
//        if (!hasRun) {
//            if (getDeltaMovement().length() < 0.01) {
//                setDeltaMovement(Vec3.ZERO);
//                if (!level().isClientSide())
//                    program.createMainThread();
//                hasRun = true;
//            } else {
//                setDeltaMovement(getDeltaMovement().scale(0.5));
//            }
//        }
//        if (!level().isClientSide() && hasRun) {
//            if (program == null || !program.isRunning()) {
//                discard();
//                return;
//            }
//            handle(IHandleContext.NONE);
//            program.step(speed);
//        }
//        checkInsideBlocks();
//        updateRotation();
//        setPos(position().add(getDeltaMovement()));
//
//        super.tick();
//    }
//
//    @Override
//    public void remove(@NotNull RemovalReason reason) {
//        if (program != null && program.isRunning())
//            program.stop();
//        super.remove(reason);
//    }
//
//    @Override
//    protected void onInsideBlock(@NotNull BlockState blockstate) {
//        if (!blockstate.isAir()) {
//            VoxelShape voxelshape = blockstate.getCollisionShape(level(), blockPosition());
//            if (!voxelshape.isEmpty()) {
//                for (AABB aabb : voxelshape.toAabbs()) {
//                    if (aabb.move(blockPosition()).contains(position())) {
//                        setDeltaMovement(Vec3.ZERO);
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}
//
//    @Override
//    public boolean ignoreExplosion(@NotNull Explosion explosion) {
//        return true;
//    }
//
//    @Override
//    public boolean isNoGravity() {
//        return true;
//    }
//
//    @Override
//    public boolean isPickable() {
//        return false;
//    }
//
    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
//        hasRun = true;
        super.readAdditionalSaveData(compound);
    }
//
//    @WenyanThreading
//    abstract class ThisCallHandler implements ISimpleExecCallHandler {
//        @Override
//        public @NotNull Optional<IWenyanDevice> getExecutor() {
//            if (isRemoved())
//                return Optional.empty();
//            return Optional.of(HandRunnerEntity.this);
//        }
//    }
}
