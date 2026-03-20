package indi.wenyan.content.block.runner;

import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.Either;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;

public record BlockPackageGetter(Consumer<BlockPos> communicateConsumer) {
    public static final int DEVICE_SEARCH_RANGE = 3;

    public @Nullable Either<WenyanPackage, String> getPackage(Level level, BlockPos blockPos, String packageName) {
        for (BlockPos pos : BlockPos.betweenClosed(
                blockPos.offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                blockPos.offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            if (pos.equals(blockPos)) continue;

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
        }
        return null;
    }

    @Contract("_, _ -> new")
    public WenyanPackage processPackage(RawHandlerPackage rawPackage, IWenyanBlockDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, (RequestCallHandler) (thread, self, argsList) ->
                        new BlockRequest(thread, self, argsList, device, function.get(), communicateConsumer)));
        return new WenyanPackage(map);
    }
}
