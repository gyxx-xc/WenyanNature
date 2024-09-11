package indi.wenyan.setup;

import indi.wenyan.WenyanNature;
import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.item.WenyanHandRunner;
import indi.wenyan.network.ProgramTextClientPayloadHandler;
import indi.wenyan.network.ProgramTextServerPayloadHandler;
import indi.wenyan.network.RunnerTextPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanNature.MODID;

public class Registration {

    public static void register(IEventBus modEventBus) {
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(Registration::onRegisterPayloadHandler);
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredItem<Item> HAND_RUNNER = ITEMS.registerItem("hand_runner", WenyanHandRunner::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("wenyan_nature", () -> CreativeModeTab.builder()
            .title(Component.translatable("Wen Yan Nature")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> HAND_RUNNER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get());
// Add the example item to the tab. For your own tabs, this method is preferred over the event

                output.accept(HAND_RUNNER.get());
            }).build());

    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY =
            ENTITY.register("hand_runner",
                    () -> EntityType.Builder
                            .of(HandRunnerEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .build("hand_runner"));

    private static void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(WenyanNature.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(
                RunnerTextPacket.TYPE,
                RunnerTextPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ProgramTextClientPayloadHandler::handleRunnerTextPacket,
                        ProgramTextServerPayloadHandler::handleRunnerTextPacket));
    }
}
