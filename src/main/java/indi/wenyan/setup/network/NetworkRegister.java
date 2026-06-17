package indi.wenyan.setup.network;

import indi.wenyan.setup.network.client.*;
import indi.wenyan.setup.network.server.BlockCodePacket;
import indi.wenyan.setup.network.server.BlockRenamePacket;
import indi.wenyan.setup.network.server.FloatNotePacket;
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

    /// Registers network packet handlers
    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);

        serverside(registrar, FloatNotePacket.TYPE, FloatNotePacket.STREAM_CODEC);
        serverside(registrar, BlockCodePacket.TYPE, BlockCodePacket.STREAM_CODEC);
        serverside(registrar, BlockRenamePacket.TYPE, BlockRenamePacket.STREAM_CODEC);

        registrar.playToClient(CommunicationLocationPacket.TYPE, CommunicationLocationPacket.STREAM_CODEC);
        registrar.playToClient(CraftClearParticlePacket.TYPE, CraftClearParticlePacket.STREAM_CODEC);
        registrar.playToClient(BlockPosRangePacket.TYPE, BlockPosRangePacket.STREAM_CODEC);
        registrar.playToClient(CraftingParticlePacket.TYPE, CraftingParticlePacket.STREAM_CODEC);
        registrar.playToClient(BlockOutputPacket.TYPE, BlockOutputPacket.STREAM_CODEC);
        registrar.playToClient(BlockDebugContextPacket.TYPE, BlockDebugContextPacket.STREAM_CODEC);
        registrar.playToClient(PistonMovePacket.TYPE, PistonMovePacket.STREAM_CODEC);
        registrar.playToClient(BlockSetScreenPacket.TYPE, BlockSetScreenPacket.STREAM_CODEC);
        registrar.playToClient(FloatNoteSetScreenPacket.TYPE, FloatNoteSetScreenPacket.STREAM_CODEC);
    }

    private static <T extends IServersidePacket> void serverside(PayloadRegistrar registrar,
                                                                 CustomPacketPayload.Type<T> type,
                                                                 StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, IServersidePacket::handleOnServer);
    }
}
