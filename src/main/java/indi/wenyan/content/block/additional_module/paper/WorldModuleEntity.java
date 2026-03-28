package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.function.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WorldModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.WorldModule;

    @Getter
    private final List<String> output = new ArrayList<>();

    @Getter
    private int signal = 0;

    // redstone get/set, show text, entity detection
    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.WorldModule$signalStrength, _ -> {
                int value = getLevel() != null ? getLevel().getBestNeighborSignal(getBlockPos()) : 0;
                return WenyanValues.of(value);
            })
            .handler(WenyanSymbol.WorldModule$emitSignal, request -> {
                signal = request.args().getFirst().as(WenyanInteger.TYPE).value();
                assert getLevel() != null;
                WorldModuleBlock.updateNeighbors(getBlockState(), getLevel(), getBlockPos());
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.WorldModule$changeWeather, 5, (_, request) -> {
                if (!(getLevel() instanceof ServerLevel serverLevel))
                    throw new WenyanUnreachedException();
                String cmd = request.args().getFirst().as(WenyanString.TYPE).value();
                switch (cmd) {
                    case "晴" -> serverLevel.getWeatherData().setRaining(false);
                    case "雨" -> serverLevel.getWeatherData().setRaining(true);
                    case "雷" -> serverLevel.getWeatherData().setThundering(true);
                    default ->
                            throw new WenyanException.WenyanTypeException(JudouExceptionText.ArgsNumWrongRange.string());
                }
                return WenyanNull.NULL;
            })
            .build();

    public WorldModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.INFORMATION_MODULE_ENTITY.get(), pos, blockState);
    }
}
