package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.RawHandlerPackage;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

public class WorldModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("WorldModule");

    @Getter
    private final List<String> output = new LinkedList<>();

    @Getter
    private int signal = 0;

    // redstone get/set, show text, entity detection
    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("WorldModule.signalStrength"), request -> {
                int value = getLevel() != null ? getLevel().getBestNeighborSignal(blockPos()) : 0;
                return WenyanValues.of(value);
            })
            .handler(WenyanSymbol.var("WorldModule.emitSignal"), request -> {
                signal = request.args().getFirst().as(WenyanInteger.TYPE).value();
                assert getLevel() != null;
                WorldModuleBlock.updateNeighbors(getBlockState(), getLevel(), blockPos());
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("WorldModule.trigger"), 2, (handler, request) -> {
                int dx = Math.clamp(request.args().get(0).as(WenyanInteger.TYPE).value(), -10, 10);
                int dy = Math.clamp(request.args().get(1).as(WenyanInteger.TYPE).value(), -10, 10);
                int dz = Math.clamp(request.args().get(2).as(WenyanInteger.TYPE).value(), -10, 10);
                BlockPos blockPos = blockPos().offset(dx, dy, dz);
                assert level != null;
                level.getProfiler().push("explosion_blocks");
                level.getBlockState(blockPos).onExplosionHit(level, blockPos,
                        new Explosion(level, null, blockPos.getX(), blockPos.getY(),
                                blockPos.getZ(), 1.0f, false, Explosion.BlockInteraction.TRIGGER_BLOCK),
                        (a1, a2) -> {});
                level.getProfiler().pop();
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("WorldModule.changeWeather"), 5, (handler, request) -> {
                if (getLevel() instanceof ServerLevel serverLevel) {
                    String cmd = request.args().getFirst().as(WenyanString.TYPE).value();
                    switch (cmd) {
                        case "晴" -> serverLevel.setWeatherParameters(
                                ServerLevel.RAIN_DELAY.sample(serverLevel.getRandom()), 0, false, false);
                        case "雨" -> serverLevel.setWeatherParameters(
                                0, ServerLevel.RAIN_DURATION.sample(serverLevel.getRandom()), true, false);
                        case "雷" -> serverLevel.setWeatherParameters(
                                0, ServerLevel.THUNDER_DURATION.sample(serverLevel.getRandom()), true, true);
                        default -> throw new WenyanException.WenyanTypeException("參數必須是「「晴」」「「雨」」「「雷」」");
                    }
                    return WenyanNull.NULL;
                } else {
                    throw new WenyanException("只能在伺服器中使用");
                }
            })
            .build();

    public WorldModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INFORMATION_MODULE_ENTITY.get(), pos, blockState);
    }
}
