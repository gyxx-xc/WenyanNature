package indi.wenyan.client;

import indi.wenyan.client.gui.CraftingBlockScreen;
import indi.wenyan.client.gui.ScreenOpenerFactroy;
import indi.wenyan.client.renderer.block.*;
import indi.wenyan.client.renderer.entity.ThrowRunnerRender;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.client.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Client-side event handler for registering renderers, screens, and particle providers
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public enum ClientSetup {;
    /**
     * Registers entity and block entity renderers
     */
    @SubscribeEvent
    public static void registerRender(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerEntityRenderer(Registration.HAND_RUNNER_ENTITY.get(), HandRunnerRender::new);
//        event.registerEntityRenderer(Registration.BULLET_ENTITY.get(), BulletRender::new);
        event.registerEntityRenderer(WyRegistration.THROW_RUNNER_ENTITY.get(), ThrowRunnerRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), RunnerBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.BLOCK_MODULE_ENTITY.get(), BlockModuleRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.PEDESTAL_ENTITY.get(), PedestalBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.WRITING_BLOCK_ENTITY.get(), WritingBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get(), ScreenModuleBlockRenderer::new);
        event.registerBlockEntityRenderer(WenyanBlocks.CRAFTING_BLOCK_ENTITY.get(), CraftingBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get(), FormationCoreModuleBlockRenderer::new);
        event.registerBlockEntityRenderer(WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get(), BlockingQueueModuleRenderer::new);
    }

    /**
     * Registers menu screens
     */
    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(WyRegistration.CRAFTING_CONTAINER.get(), CraftingBlockScreen::new);
    }

    /**
     * Registers particle providers
     */
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
//        event.registerSpriteSet(WYRegistration.COMMUNICATION_PARTICLES.get(), CommunicationParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onClientHandler(RegisterClientPayloadHandlersEvent event) {
        event.register(BlockOutputPacket.TYPE, BlockOutputPacket.HANDLER);
        event.register(CommunicationLocationPacket.TYPE, CommunicationLocationPacket.HANDLER);
        event.register(CraftClearParticlePacket.TYPE, CraftClearParticlePacket.HANDLER);
        event.register(BlockPosRangePacket.TYPE, BlockPosRangePacket.HANDLER);
        event.register(CraftingParticlePacket.TYPE, CraftingParticlePacket.HANDLER);
        event.register(PlatformOutputPacket.TYPE, PlatformOutputPacket.HANDLER);
        event.register(PistonMovePacket.TYPE, PistonMovePacket.HANDLER);
        event.register(BlockSetScreenPacket.TYPE, ScreenOpenerFactroy.BLOCK_HANDLER);
    }
}
