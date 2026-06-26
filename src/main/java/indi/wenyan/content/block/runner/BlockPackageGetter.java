package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.ICodeHolder;
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
        int range = WenyanConfig.getRunnerRange();
        for (BlockPos pos : BlockPos.betweenClosed(
                blockPos.offset(range, -range, range),
                blockPos.offset(-range, range, -range))) {
            ImportRequest.IWenyanPackageable either = getWenyanPackageEither(level, blockPos, pos, packageName);
            if (either != null) return either;
        }
        return null;
    }

    private @Nullable ImportRequest.IWenyanPackageable getWenyanPackageEither(Level level, BlockPos blockPos, BlockPos pos, String packageName) {
        if (pos.equals(blockPos)) return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        // If able to run, return Executor first
        if (blockEntity instanceof RunnerBlockEntity entity && entity.getPlatformName().equals(packageName)) {
            communicateConsumer.accept(pos);
            return new ImportRequest.IWenyanPackageable.Executor(entity);
        }

        // Then if not able to run, return as code package
        if (blockEntity instanceof ICodeHolder platform && platform.getPlatformName().equals(packageName)) {
            communicateConsumer.accept(pos);
            return new ImportRequest.IWenyanPackageable.Code(platform.getCode());
        }

        var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, pos);
        if (executor != null && executor.getPackageName().equals(packageName)) {
            communicateConsumer.accept(pos);
            return new ImportRequest.IWenyanPackageable.Package(processPackage(executor.getExecPackage(), executor));
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
