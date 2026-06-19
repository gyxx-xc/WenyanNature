package indi.wenyan.content.block.runner;

import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.Either;
import indi.wenyan.judou.api.values.WenyanPackage;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;

public record BlockPackageGetter(Consumer<BlockPos> communicateConsumer) {
    public @Nullable Either<WenyanPackage, String> getPackage(Level level, BlockPos blockPos, String packageName) {
        int range = WenyanConfig.getRunnerRange();
        for (BlockPos pos : BlockPos.betweenClosed(
                blockPos.offset(range, -range, range),
                blockPos.offset(-range, range, -range))) {
            Either<WenyanPackage, String> either = getWenyanPackageEither(level, blockPos, pos, packageName);
            if (either != null) return either;
        }
        return null;
    }

    private @Nullable Either<WenyanPackage, String> getWenyanPackageEither(Level level, BlockPos blockPos, BlockPos pos, String packageName) {
        if (pos.equals(blockPos)) return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RunnerBlockEntity platform && platform.getPlatformName().equals(packageName)) {
            communicateConsumer.accept(pos);
            return Either.right(platform.getCode());
        }

        var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, pos);
        if (executor != null && executor.getPackageName().equals(packageName)) {
            communicateConsumer.accept(pos);
            return Either.left(processPackage(executor.getExecPackage(), executor));
        }
        return null;
    }

    @Contract("_, _ -> new")
    public WenyanPackage processPackage(RawHandlerPackage rawPackage, IWenyanBlockDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, (IRequestCallHandler) (thread, self, argsList, onReturn) ->
                        new BlockRequest(thread, self, argsList, device, function.get(), communicateConsumer, onReturn)));
        return new WenyanPackage(map);
    }
}
