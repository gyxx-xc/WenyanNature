package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
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
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function(WenyanSymbol.var("WorldModule.signalStrength"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    int value = 0;
                    if (getLevel() != null) {
                        value = getLevel().getBestNeighborSignal(getBlockPos());
                    }
                    return WenyanValues.of(value);
                }
            })
            .function(WenyanSymbol.var("WorldModule.emitSignal"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    signal = context.args().getFirst().as(WenyanInteger.TYPE).value();
                    assert getLevel() != null;
                    WorldModuleBlock.updateNeighbors(getBlockState(), getLevel(), getBlockPos());
                    return WenyanNull.NULL;
                }
            })
            .function(WenyanSymbol.var("WorldModule.trigger"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    int dx = Math.clamp(context.args().get(0).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    int dy = Math.clamp(context.args().get(1).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    int dz = Math.clamp(context.args().get(2).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    BlockPos blockPos = getBlockPos().offset(dx, dy, dz);
                    assert level != null;
                    level.getProfiler().push("explosion_blocks");
                    level.getBlockState(blockPos).onExplosionHit(level, blockPos,
                            new Explosion(level, null, blockPos.getX(), blockPos.getY(),
                                    blockPos.getZ(),
                                    1.0f, false, Explosion.BlockInteraction.TRIGGER_BLOCK), (a1, a2) -> {
                            });
                    level.getProfiler().pop();
                    return WenyanNull.NULL;
                }
            })
            .function(WenyanSymbol.var("WorldModule.changeWeather"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    if (getLevel() instanceof ServerLevel serverLevel) {
                        String cmd = context.args().getFirst().as(WenyanString.TYPE).value();
                        switch (cmd) {
                            case "晴" -> serverLevel.setWeatherParameters(ServerLevel.RAIN_DELAY.sample(serverLevel.getRandom()), 0, false, false);
                            case "雨" -> serverLevel.setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(serverLevel.getRandom()), true, false);
                            case "雷" -> serverLevel.setWeatherParameters(0, ServerLevel.THUNDER_DURATION.sample(serverLevel.getRandom()), true, true);
                            default -> throw new WenyanException.WenyanTypeException("參數必須是「晴」「雨」「雷」");
                        }
                        return WenyanNull.NULL;
                    } else {
                        throw new WenyanException("只能在伺服器中使用");
                    }
                }
            })
            .build();

    public WorldModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INFORMATION_MODULE_ENTITY.get(), pos, blockState);
    }
}
