package indi.wenyan.setup.event;

import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.network.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Common mod setup handling events
 */
@EventBusSubscriber(modid = MODID)
public enum ModSetup {;
    /**
     * Registers capabilities for mod blocks and entities
     */
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.PEDESTAL_ENTITY.get(),
                (be, _) -> be.getItemHandler());
    }

    /**
     * Registers network packet handlers
     */
    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(RunnerCodePacket.TYPE,
                RunnerCodePacket.STREAM_CODEC,
                RunnerCodePacket.HANDLER);
        registrar.playToServer(FloatNotePacket.TYPE,
                FloatNotePacket.STREAM_CODEC,
                FloatNotePacket.HANDLER);
        registrar.playToServer(BlockRunnerCodePacket.TYPE,
                BlockRunnerCodePacket.STREAM_CODEC,
                BlockRunnerCodePacket.HANDLER);
        registrar.playToServer(RunnerTitlePacket.TYPE,
                RunnerTitlePacket.STREAM_CODEC,
                RunnerTitlePacket.HANDLER);
        registrar.playToServer(PlatformRenamePacket.TYPE,
                PlatformRenamePacket.STREAM_CODEC,
                PlatformRenamePacket.HANDLER);
        registrar.playToServer(DeviceRenamePacket.TYPE,
                DeviceRenamePacket.STREAM_CODEC,
                DeviceRenamePacket.HANDLER);
        registrar.commonToClient(BlockOutputPacket.TYPE,
                BlockOutputPacket.STREAM_CODEC,
                BlockOutputPacket.HANDLER);
        registrar.commonToClient(CommunicationLocationPacket.TYPE,
                CommunicationLocationPacket.STREAM_CODEC,
                CommunicationLocationPacket.HANDLER);
        registrar.commonToClient(CraftClearParticlePacket.TYPE,
                CraftClearParticlePacket.STREAM_CODEC,
                CraftClearParticlePacket.HANDLER);
        registrar.commonToClient(BlockPosRangePacket.TYPE,
                BlockPosRangePacket.STREAM_CODEC,
                BlockPosRangePacket.HANDLER);
        registrar.commonToClient(CraftingParticlePacket.TYPE,
                CraftingParticlePacket.STREAM_CODEC,
                CraftingParticlePacket.HANDLER);
        registrar.commonToClient(PlatformOutputPacket.TYPE,
                PlatformOutputPacket.STREAM_CODEC,
                PlatformOutputPacket.HANDLER);
    }
}
