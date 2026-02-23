package indi.wenyan.setup.event;

import indi.wenyan.client.block.renderer.*;
import indi.wenyan.client.gui.CraftingBlockScreen;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

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
        event.registerBlockEntityRenderer(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), RunnerBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.BLOCK_MODULE_ENTITY.get(), BlockModuleRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.PEDESTAL_ENTITY.get(), PedestalBlockRender::new);
        event.registerBlockEntityRenderer(WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get(), ScreenModuleBlockRenderer::new);
        event.registerBlockEntityRenderer(WenyanBlocks.CRAFTING_BLOCK_ENTITY.get(), CraftingBlockRender::new);
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
}
