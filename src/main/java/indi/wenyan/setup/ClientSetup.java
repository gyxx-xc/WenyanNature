package indi.wenyan.setup;

import indi.wenyan.entity.BulletRender;
import indi.wenyan.entity.HandRunnerModel;
import indi.wenyan.entity.HandRunnerRender;
import indi.wenyan.entity.HandlerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.references.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

import static indi.wenyan.WenyanNature.LOGGER;
import static indi.wenyan.WenyanNature.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        // Some client setup code
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HandRunnerModel.LAYER_LOCATION, HandRunnerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.HAND_RUNNER_ENTITY.get(), HandRunnerRender::new);
        event.registerEntityRenderer(Registration.BULLET_ENTITY.get(), BulletRender::new);
        event.registerEntityRenderer(Registration.HANDLER_ENTITY.get(), context -> new EntityRenderer<>(context) {
            @Override
            public @NotNull ResourceLocation getTextureLocation(@NotNull HandlerEntity handlerEntity) {
                return ResourceLocation.fromNamespaceAndPath(MODID, "item/hand_runner");
            }
        });
    }

}
