package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockOutputPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedList;
import java.util.List;

public class InformativeAdditionalModuleEntity extends AbstractAdditionalModuleEntity{
    @Getter
    private final String packageName = "「信」";

    @Getter
    private final List<String> output = new LinkedList<>();

    @Getter
    private int signal = 0;

    // redstone get/set, show text, entity detection
    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「量」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    int value = 0;
                    if (getLevel() != null) {
                        value = getLevel().getBestNeighborSignal(getBlockPos());
                    }
                    return new WenyanInteger(value);
                }
            })
            .function("「輸能」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    signal = context.args().getFirst().as(WenyanInteger.TYPE).value();
                    assert getLevel() != null;
                    InformativeAdditionalModuleBlock.updateNeighbors(getBlockState(), getLevel(), getBlockPos());
                    return WenyanNull.NULL;
                }
            })
            .function("書", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    StringBuilder result = new StringBuilder();
                    for (IWenyanValue arg : context.args()) {
                        result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
                    }
                    if (getLevel() instanceof ServerLevel sl) {
                        PacketDistributor.sendToPlayersTrackingChunk(sl,
                                new ChunkPos(getBlockPos()), new BlockOutputPacket(getBlockPos(),
                                 result.toString()));
                    }
                    return WenyanNull.NULL;
                }
            })
            .build();

    public void addOutput(String text) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        output.addLast(text);
        if (output.size() > 10) {
            output.removeFirst();
        }
    }

    public InformativeAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INFORMATIVE_MODULE_ENTITY.get(), pos, blockState);
    }
}
