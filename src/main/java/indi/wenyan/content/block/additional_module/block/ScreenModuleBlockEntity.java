package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.runner.IOutputAccepter;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.network.client.BlockOutputPacket;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenModuleBlockEntity extends AbstractModuleEntity implements IOutputAccepter {
    public static final int OUTPUT_MAX_LENGTH = 30;
    public static final int MAX_OUTPUT_SIZE = 10;

    public ScreenModuleBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final Deque<Component> outputQueue = new ArrayDeque<>();

    @Getter
    private final String basePackageName = WenyanSymbol.SCREEN;

    @Getter
    public final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.PRINT, (HandlerPackageBuilder.HandlerReturnFunction) (ignore, request) -> {
                StringBuilder result = new StringBuilder();
                for (IWenyanValue arg : request.args()) {
                    result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
                    if (result.length() >= OUTPUT_MAX_LENGTH) break;
                }
                if (getLevel() instanceof ServerLevel sl) {
                    PacketDistributor.sendToPlayersTrackingChunk(sl,
                            ChunkPos.containing(getBlockPos()), new BlockOutputPacket(getBlockPos(),
                                    StringUtils.left(result.toString(), OUTPUT_MAX_LENGTH), OutputStyle.NORMAL));
                }
                return WenyanNull.NULL;
            })
            .build();

    public void addOutput(String output, IOutputAccepter.OutputStyle style) {
        if (style == IOutputAccepter.OutputStyle.ERROR)
            outputQueue.addLast(Component.literal(output).withStyle(ChatFormatting.RED));
        else if (style == IOutputAccepter.OutputStyle.NORMAL)
            outputQueue.addLast(Component.literal(output));
        while (outputQueue.size() > MAX_OUTPUT_SIZE) {
            outputQueue.removeFirst();
        }
    }
}
