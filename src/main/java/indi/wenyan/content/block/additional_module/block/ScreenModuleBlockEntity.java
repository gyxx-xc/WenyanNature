package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
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

public class ScreenModuleBlockEntity extends AbstractModuleEntity implements BlockOutputPacket.IDisplayable {
    public ScreenModuleBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.SCREEN_MODULE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final List<String> output = new LinkedList<>();

    @Getter
    public final String basePackageName = "builtin";

    @Getter
    public final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("書", 1, (ignore, request) -> {
                StringBuilder result = new StringBuilder();
                for (IWenyanValue arg : request.args()) {
                    result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
                }
                if (getLevel() instanceof ServerLevel sl) {
                    PacketDistributor.sendToPlayersTrackingChunk(sl,
                            new ChunkPos(blockPos()), new BlockOutputPacket(blockPos(),
                                    result.toString()));
                }
                return WenyanNull.NULL;
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
}
