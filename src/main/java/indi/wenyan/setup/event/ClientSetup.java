package indi.wenyan.setup.event;

import indi.wenyan.content.block.additional_module.block.ScreenModuleBlockRenderer;
import indi.wenyan.content.block.additional_module.paper.BlockModuleRender;
import indi.wenyan.content.block.crafting_block.CraftingBlockRender;
import indi.wenyan.content.block.pedestal.PedestalBlockRender;
import indi.wenyan.content.block.runner.RunnerBlockRender;
import indi.wenyan.content.gui.CraftingBlockScreen;
import indi.wenyan.content.particle.CommunicationParticle;
import indi.wenyan.setup.definitions.WYRegistration;
import indi.wenyan.setup.definitions.WenyanBlocks;
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
        event.registerBlockEntityRenderer(WenyanBlocks.PEDESTAL_ENTITY.get(), context -> new PedestalBlockRender());
        event.registerBlockEntityRenderer(WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get(), ScreenModuleBlockRenderer::new);
        event.registerBlockEntityRenderer(WenyanBlocks.CRAFTING_BLOCK_ENTITY.get(), CraftingBlockRender::new);
    }

    /**
     * Registers menu screens
     */
    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(WYRegistration.CRAFTING_CONTAINER.get(), CraftingBlockScreen::new);
    }

    /**
     * Registers particle providers
     */
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(WYRegistration.COMMUNICATION_PARTICLES.get(), CommunicationParticle.Provider::new);
    }
}
