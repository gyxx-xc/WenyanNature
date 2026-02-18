package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockOutputPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScreenModuleBlockEntity extends AbstractModuleEntity implements BlockOutputPacket.IDisplayable {
    public static final int OUTPUT_MAX_LENGTH = 30;

    public ScreenModuleBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.SCREEN_MODULE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final List<String> output = new ArrayList<>();

    @Override
    public @NotNull String getBasePackageName() {
        return "";
    }

    @Getter
    public final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("書", (HandlerPackageBuilder.HandlerReturnFunction) (ignore, request) -> {
                StringBuilder result = new StringBuilder();
                for (IWenyanValue arg : request.args()) {
                    result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
                    if (result.length() >= OUTPUT_MAX_LENGTH) break;
                }
                if (getLevel() instanceof ServerLevel sl) {
                    PacketDistributor.sendToPlayersTrackingChunk(sl,
                            new ChunkPos(blockPos()), new BlockOutputPacket(blockPos(),
                                    StringUtils.left(result.toString(), OUTPUT_MAX_LENGTH)));
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
