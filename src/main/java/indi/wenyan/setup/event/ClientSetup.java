package indi.wenyan.setup.event;

import indi.wenyan.content.block.additional_module.BlockModuleRender;
import indi.wenyan.content.block.additional_module.InformationModuleRender;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlockRenderer;
import indi.wenyan.content.block.pedestal.PedestalBlockRender;
import indi.wenyan.content.block.runner.RunnerBlockRender;
import indi.wenyan.content.entity.BulletRender;
import indi.wenyan.content.entity.HandRunnerRender;
import indi.wenyan.content.gui.CraftingBlockScreen;
import indi.wenyan.content.particle.CommunicationParticle;
import indi.wenyan.setup.Registration;
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
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public enum ClientSetup {;
    /**
     * Registers entity and block entity renderers
     */
    @SubscribeEvent
    public static void registerRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.HAND_RUNNER_ENTITY.get(), HandRunnerRender::new);
        event.registerEntityRenderer(Registration.BULLET_ENTITY.get(), BulletRender::new);
        event.registerBlockEntityRenderer(Registration.RUNNER_BLOCK_ENTITY.get(), RunnerBlockRender::new);
        event.registerBlockEntityRenderer(Registration.BLOCK_MODULE_ENTITY.get(), BlockModuleRender::new);
        event.registerBlockEntityRenderer(Registration.INFORMATION_MODULE_ENTITY.get(), InformationModuleRender::new);
        event.registerBlockEntityRenderer(Registration.PEDESTAL_ENTITY.get(), PedestalBlockRender::new);
        event.registerBlockEntityRenderer(Registration.SCREEN_MODULE_BLOCK_ENTITY.get(), ScreenModuleBlockRenderer::new);
    }

    /**
     * Registers menu screens
     */
    @SubscribeEvent
    public static void registerScreen(RegisterMenuScreensEvent event) {
        event.register(Registration.CRAFTING_CONTAINER.get(), CraftingBlockScreen::new);
    }

    /**
     * Registers particle providers
     */
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Registration.COMMUNICATION_PARTICLES.get(), CommunicationParticle.Provider::new);
    }
}
