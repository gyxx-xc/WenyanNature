package indi.wenyan.content.entity;

import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.runner.BlockPackageGetter;
import indi.wenyan.content.block.runner.ICodeHolder;
import indi.wenyan.content.block.runner.LazyProgram;
import indi.wenyan.content.item.throw_runner.FuContainerComponent;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.IWenyanDevice;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.ImportRequest;
import indi.wenyan.judou.exec_interface.structure.SimpleRequest;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.Either;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanEntities;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.language.ExceptionText;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ThrowRunnerEntity extends ThrowableItemProjectile
        implements IWenyanPlatform, ICommunicateHolder {
    public static final String ID = "throw_runner_entity";
    private final int lifetime = WenyanConfig.getThrowEntityLifetime();

    @Getter private String platformName;
    @Getter private final ExecQueue execQueue = new ExecQueue(this);
    @Getter private final List<CommunicationEffect> communicates = new ArrayList<>();

    @Nullable
    private final Player player;
    private final RunnerTier tier;
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    private final LazyProgram<IWenyanProgram<WenyanProgramImpl.PCB>> lazyProgram =
            new LazyProgram<>(() -> new WenyanProgramImpl(this));
    private final Map<String, IWenyanDevice> packages = new HashMap<>();
    private final BlockPackageGetter blockPackageGetter = new BlockPackageGetter(_ -> {
    });

    private int life = 0;

    public ThrowRunnerEntity(EntityType<ThrowRunnerEntity> entityType, Level level) {
        super(entityType, level);
        this.tier = RunnerTier.RUNNER_0;
        player = null;
    }

    public ThrowRunnerEntity(Level level, Position pos, @NotNull ItemStack itemStack, @NotNull RunnerTier tier) {
        super(WenyanEntities.THROW_RUNNER_ENTITY.get(), pos.x(), pos.y(), pos.z(), level, itemStack);
        player = null;
        this.tier = tier;
        if (!level.isClientSide()) {
            var code = itemStack.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
            if (code != null) {
                platformName = code.getPlatformName();
                startProgram(itemStack, code);
            } else {
                platformName = "";
                // and discard in tick
            }
        } else {
            platformName = "";
        }
    }

    public ThrowRunnerEntity(Level level, LivingEntity owner, @NotNull ItemStack itemStack, @NotNull RunnerTier tier) {
        super(WenyanEntities.THROW_RUNNER_ENTITY.get(), owner, level, itemStack);
        this.tier = tier;
        if (!level.isClientSide()) {
            if (owner instanceof Player p)
                this.player = p;
            else
                this.player = null;
            var code = itemStack.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
            if (code != null) {
                platformName = code.getPlatformName();
                startProgram(itemStack, code);
            } else {
                platformName = "";
                // and discard in tick
            }
        } else {
//            setRemainingFireTicks(1);
            this.player = null;
            platformName = "";
        }
    }

    private void startProgram(@NonNull ItemStack itemStack, ICodeHolder code) {
        setRemainingFireTicks(1);
        try {
            lazyProgram.create().create(WenyanRunner.of(WenyanFrame.ofCode(code.getCode()), this.initEnvironment()));
        } catch (WenyanException | WenyanCompileException e) {
            handleError(e.getMessage());
            // add will show this message and kill itself at tick
        }
        List<ItemStack> items = itemStack.getOrDefault(WyRegistration.FU_DATA, FuContainerComponent.EMPTY).createOne();
        for (ItemStack stack : items) {
            IWenyanDevice device = stack.getCapability(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY);
            if (device != null) {
                String packageName = device.getPackageName();
                if (!packages.containsKey(packageName))
                    packages.put(packageName, device);
                else
                    handleError(ExceptionText.PackageAlreadtRegistered.string(packageName));
            }
        }
    }

    @Override
    public void handleError(String error) {
        errors.add(error);
    }

    @Override
    public WenyanPackage initEnvironment() {
        var basePackage = IWenyanPlatform.super.initEnvironment();
        basePackage.put(WenyanPackages.IMPORT_ID, (RequestCallHandler) (t, _, a) ->
                new ImportRequest(t, this::getPackage, a));
        basePackage.put(WenyanSymbol.PRINT, (RequestCallHandler) (thread, self, argsList) ->
                new SimpleRequest(thread, self, argsList,
                        (ignore, args) -> {
                            if (player != null) {
                                String s = args.getFirst().as(WenyanString.TYPE).value();
                                player.sendSystemMessage(Component.literal(s));
                            }
                            return WenyanNull.NULL;
                        }));
        return basePackage;
    }

    @Override
    public void tick() {
        if (life++ > lifetime)
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

    private @NonNull ThrowEntityContext getContext() {
        return new ThrowEntityContext();
    }

    private Either<WenyanPackage, String> getPackage(IHandleContext iHandleContext, String s) throws WenyanException {
        // check local first
        var localDevice = packages.get(s);
        if (localDevice != null)
            return Either.left(processInternalPackage(localDevice.getExecPackage(), localDevice));

        // check external
        var externalPackage = blockPackageGetter.getPackage(level(), BlockPos.containing(getPosition(0)), s);
        if (externalPackage != null)
            return externalPackage;
        throw new WenyanException(ExceptionText.ImportNotFound.string(s));
    }

    @Contract("_, _ -> new")
    private WenyanPackage processInternalPackage(RawHandlerPackage rawPackage, IWenyanDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, (RequestCallHandler) (thread, self, argsList) ->
                        new ThrowEntityRequest(self, argsList, thread, function.get(),
                                () -> packages.remove(device.getPackageName()) != null)));
        return new WenyanPackage(map);
    }
}
