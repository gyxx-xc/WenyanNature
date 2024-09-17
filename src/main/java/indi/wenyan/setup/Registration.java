package indi.wenyan.setup;

import com.mojang.datafixers.DSL;
import indi.wenyan.WenyanNature;
import indi.wenyan.block.RunnerBlock;
import indi.wenyan.entity.BulletEntity;
import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.entity.HandlerEntity;
import indi.wenyan.item.WenyanHandRunner;
import indi.wenyan.network.ProgramTextClientPayloadHandler;
import indi.wenyan.network.ProgramTextServerPayloadHandler;
import indi.wenyan.network.RunnerTextPacket;
import indi.wenyan.block.BlockRunner;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanNature.MODID;

public class Registration {

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        ENTITY.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(Registration::onRegisterPayloadHandler);
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredItem<Item> HAND_RUNNER = ITEMS.registerItem("hand_runner", WenyanHandRunner::new);
    public static final DeferredBlock<RunnerBlock> RUNNER_BLOCK = BLOCKS.register("runner_block",
            () -> new RunnerBlock(RunnerBlock.PROPERTIES));
    public static final Supplier<BlockEntityType<BlockRunner>> BLOCK_RUNNER =
            BLOCK_ENTITY.register("block_runner",
            () -> BlockEntityType.Builder
                    .of(BlockRunner::new, RUNNER_BLOCK.get())
                    .build(DSL.remainderType()));
    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY =
            ENTITY.register("hand_runner",
                    () -> EntityType.Builder
                            .of((EntityType.EntityFactory<HandRunnerEntity>) HandRunnerEntity::new, MobCategory.MISC)
                            .sized(0.45f, 1f)
                            .build("hand_runner"));

    public static final Supplier<EntityType<BulletEntity>> BULLET_ENTITY =
            ENTITY.register("bullet_entity",
                    () -> EntityType.Builder
                            .of((EntityType.EntityFactory<BulletEntity>) BulletEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .build("bullet_entity"));

    public static final Supplier<EntityType<HandlerEntity>> HANDLER_ENTITY =
            ENTITY.register("handler_entity",
                    () -> EntityType.Builder
                            .of(HandlerEntity::new, MobCategory.MISC)
                            .build("handler_entity"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("wenyan_nature", () -> CreativeModeTab.builder()
            .title(Component.translatable("title.wenyan_nature.create_tab")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> HAND_RUNNER.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(HAND_RUNNER.get())).build());

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
