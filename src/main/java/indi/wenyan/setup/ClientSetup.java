package indi.wenyan.setup;

import indi.wenyan.content.block.RunnerBlockRender;
import indi.wenyan.content.entity.BulletRender;
import indi.wenyan.content.entity.HandRunnerRender;
import indi.wenyan.content.entity.HandlerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

import static indi.wenyan.WenyanNature.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.HAND_RUNNER_ENTITY.get(), HandRunnerRender::new);
        event.registerEntityRenderer(Registration.BULLET_ENTITY.get(), BulletRender::new);
        event.registerBlockEntityRenderer(Registration.BLOCK_RUNNER.get(), RunnerBlockRender::new);
        event.registerEntityRenderer(Registration.HANDLER_ENTITY.get(), context -> new EntityRenderer<>(context) {
            @Override
            public @NotNull ResourceLocation getTextureLocation(@NotNull HandlerEntity handlerEntity) {
                return ResourceLocation.fromNamespaceAndPath(MODID, "item/hand_runner");
            }
        });
    }

}
