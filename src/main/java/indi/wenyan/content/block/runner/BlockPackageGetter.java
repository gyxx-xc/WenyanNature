package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.ICodeHolder;
import indi.wenyan.content.block.cloud_beacon.GlobalPackageManager;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.interpreter_impl.ImportRequest;
import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
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
    public @Nullable ImportRequest.IWenyanPackageable getPackage(Level level, BlockPos blockPos, String packageName) {
        var manager = GlobalPackageManager.getInstance();
        var packagePos = manager.getPackage(packageName);
        if (packagePos != null) {
            ImportRequest.IWenyanPackageable either = getWenyanPackageEither(level, blockPos, packagePos, packageName, true);
            if (either != null) {
                communicateConsumer.accept(new BlockPos(blockPos.getX(), 1024, blockPos.getZ()));
                return either;
            }
        }

        int range = WenyanConfig.getRunnerRange();
        for (BlockPos pos : BlockPos.betweenClosed(
                blockPos.offset(range, -range, range),
                blockPos.offset(-range, range, -range))) {
            ImportRequest.IWenyanPackageable either = getWenyanPackageEither(level, blockPos, pos, packageName, false);
            if (either != null) {
                communicateConsumer.accept(pos);
                return either;
            }
        }
        return null;
    }

    private @Nullable ImportRequest.IWenyanPackageable getWenyanPackageEither(Level level, BlockPos blockPos, BlockPos pos, String packageName, boolean globalPos) {
        if (pos.equals(blockPos)) return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        return switch (blockEntity) {
            // If able to run, return Executor first
            case RunnerBlockEntity entity when entity.getPlatformName().equals(packageName) ->
                    new ImportRequest.IWenyanPackageable.Executor(entity, globalPos);
            // Then if not able to run, return as code package
            case ICodeHolder platform when platform.getPlatformName().equals(packageName) ->
                    new ImportRequest.IWenyanPackageable.Code(platform.getCode());
            case null, default -> {
                var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, pos);
                if (executor != null && executor.getPackageName().equals(packageName)) {
                    yield new ImportRequest.IWenyanPackageable.Package(processPackage(executor.getExecPackage(), executor));
                }
                yield null;
            }
        };
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
