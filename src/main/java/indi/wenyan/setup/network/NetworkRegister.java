package indi.wenyan.setup.network;

import indi.wenyan.setup.network.client.*;
import indi.wenyan.setup.network.server.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static indi.wenyan.WenyanProgramming.MODID;

@EventBusSubscriber(modid = MODID)
public enum NetworkRegister {
    ;

    /**
     * Registers network packet handlers
     */
    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);

        serverbound(registrar, RunnerCodePacket.TYPE, RunnerCodePacket.STREAM_CODEC);
        serverbound(registrar, FloatNotePacket.TYPE, FloatNotePacket.STREAM_CODEC);
        serverbound(registrar, BlockRunnerCodePacket.TYPE, BlockRunnerCodePacket.STREAM_CODEC);
        serverbound(registrar, RunnerTitlePacket.TYPE, RunnerTitlePacket.STREAM_CODEC);
        serverbound(registrar, PlatformRenamePacket.TYPE, PlatformRenamePacket.STREAM_CODEC);
        serverbound(registrar, DeviceRenamePacket.TYPE, DeviceRenamePacket.STREAM_CODEC);
        serverbound(registrar, WritingCodePacket.TYPE, WritingCodePacket.STREAM_CODEC);
        serverbound(registrar, WritingTitlePacket.TYPE, WritingTitlePacket.STREAM_CODEC);

        registrar.playToClient(BlockOutputPacket.TYPE, BlockOutputPacket.STREAM_CODEC);
        registrar.playToClient(CommunicationLocationPacket.TYPE, CommunicationLocationPacket.STREAM_CODEC);
        registrar.playToClient(CraftClearParticlePacket.TYPE, CraftClearParticlePacket.STREAM_CODEC);
        registrar.playToClient(BlockPosRangePacket.TYPE, BlockPosRangePacket.STREAM_CODEC);
        registrar.playToClient(CraftingParticlePacket.TYPE, CraftingParticlePacket.STREAM_CODEC);
        registrar.playToClient(PlatformOutputPacket.TYPE, PlatformOutputPacket.STREAM_CODEC);
        registrar.playToClient(PistonMovePacket.TYPE, PistonMovePacket.STREAM_CODEC);
        registrar.playToClient(BlockSetScreenPacket.TYPE, BlockSetScreenPacket.STREAM_CODEC);
    }

    private static <T extends IServerboundPacket> void serverbound(PayloadRegistrar registrar,
                                                                   CustomPacketPayload.Type<T> type,
                                                                   StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, IServerboundPacket::handleOnServer);
    }
}
