package indi.wenyan.setup;

import com.mojang.datafixers.DSL;
import indi.wenyan.WenyanNature;
import indi.wenyan.content.block.*;
import indi.wenyan.content.data.OutputData;
import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.item.WenyanHandRunner;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.setup.network.OutputInformationHandler;
import indi.wenyan.setup.network.OutputInformationPacket;
import indi.wenyan.setup.network.ProgramTextServerPayloadHandler;
import indi.wenyan.setup.network.RunnerTextPacket;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
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
        MENU_TYPE.register(modEventBus);
        DATA.register(modEventBus);

        modEventBus.addListener(Registration::onRegisterPayloadHandler);
    }

    public static final DeferredRegister.Blocks BLOCKS;
    public static final DeferredRegister.Items ITEMS;
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY;
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS;
    public static final DeferredRegister<EntityType<?>> ENTITY;
    public static final DeferredRegister<MenuType<?>> MENU_TYPE;
    public static final DeferredRegister<DataComponentType<?>> DATA;
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER;
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE;

    public static final DeferredItem<Item> HAND_RUNNER;
    public static final DeferredItem<Item> HAND_RUNNER_1;
    public static final DeferredItem<Item> HAND_RUNNER_2;
    public static final DeferredItem<Item> HAND_RUNNER_3;

    public static final DeferredBlock<RunnerBlock> RUNNER_BLOCK;
    public static final Supplier<BlockEntityType<BlockRunner>> BLOCK_RUNNER;
    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY;

    public static final DeferredBlock<CraftingBlock> CRAFTING_BLOCK;
    public static final Supplier<BlockEntityType<CraftingBlockEntity>> CRAFTING_ENTITY;
    public static final DeferredBlock<PedestalBlock> PEDESTAL_BLOCK;
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY;

    public static final Supplier<EntityType<BulletEntity>> BULLET_ENTITY;

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RunnerTierData>> TIER_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OutputData>> OUTPUT_DATA;

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnsweringRecipe>> ANSWERING_RECIPE_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnsweringRecipe>> ANSWERING_RECIPE_TYPE;

    private static void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(WenyanNature.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(
                RunnerTextPacket.TYPE,
                RunnerTextPacket.STREAM_CODEC,
                new ProgramTextServerPayloadHandler());
        registrar.commonToClient(
                OutputInformationPacket.TYPE,
                OutputInformationPacket.STREAM_CODEC,
                new OutputInformationHandler()
        );
    }

    static {
        CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
        ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
        BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
        MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
        ITEMS = DeferredRegister.createItems(MODID);
        BLOCKS = DeferredRegister.createBlocks(MODID);
        DATA = DeferredRegister.createDataComponents(MODID);
        SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
        RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

        HAND_RUNNER = ITEMS.registerItem("hand_runner_0",
                (Item.Properties properties) -> new WenyanHandRunner(properties, 0));
        HAND_RUNNER_1 = ITEMS.registerItem("hand_runner",
                (Item.Properties properties) -> new WenyanHandRunner(properties, 1));
        HAND_RUNNER_2 = ITEMS.registerItem("hand_runner_2",
                (Item.Properties properties) -> new WenyanHandRunner(properties, 2));
        HAND_RUNNER_3 = ITEMS.registerItem("hand_runner_3",
                (Item.Properties properties) -> new WenyanHandRunner(properties, 3));

        RUNNER_BLOCK = BLOCKS.register("runner_block", RunnerBlock::new);
        BLOCK_RUNNER = BLOCK_ENTITY.register("block_runner",
                () -> BlockEntityType.Builder
                        .of(BlockRunner::new, RUNNER_BLOCK.get())
                        .build(DSL.remainderType()));
        HAND_RUNNER_ENTITY = ENTITY.register("hand_runner",
                () -> EntityType.Builder
                        .of((EntityType.EntityFactory<HandRunnerEntity>) HandRunnerEntity::new, MobCategory.MISC)
                        .sized(0.45f, 1f)
                        .build("hand_runner"));

        // TODO: add crafting block variable
        CRAFTING_BLOCK = BLOCKS.register("crafting_block", CraftingBlock::new);
        ITEMS.registerItem("crafting_block", (properties) -> new BlockItem(CRAFTING_BLOCK.get(), properties));
        CRAFTING_ENTITY = BLOCK_ENTITY.register("crafting_block",
                () -> BlockEntityType.Builder
                        .of(CraftingBlockEntity::new, CRAFTING_BLOCK.get())
                        .build(DSL.remainderType()));
        CRAFTING_CONTAINER = MENU_TYPE.register("crafting_block",
                () -> IMenuTypeExtension.create(CraftingBlockContainer::new));

        BULLET_ENTITY = ENTITY.register("bullet_entity",
                () -> EntityType.Builder
                        .of((EntityType.EntityFactory<BulletEntity>) BulletEntity::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .build("bullet_entity"));
        PEDESTAL_BLOCK = BLOCKS.register("pedestal_block", PedestalBlock::new);
        ITEMS.registerItem("pedestal_block", (properties) -> new BlockItem(PEDESTAL_BLOCK.get(), properties));
        PEDESTAL_ENTITY = BLOCK_ENTITY.register("pedestal_block",
                () -> BlockEntityType.Builder
                        .of(PedestalBlockEntity::new, PEDESTAL_BLOCK.get())
                        .build(DSL.remainderType()));

        TIER_DATA = DATA.register("runner_tier_data",
                () -> DataComponentType.<RunnerTierData>builder()
                        .persistent(RunnerTierData.CODEC)
                        .build());
        OUTPUT_DATA = DATA.register("output_data",
                () -> DataComponentType.<OutputData>builder()
                        .persistent(OutputData.CODEC)
                        .networkSynchronized(OutputData.STREAM_CODEC)
                        .build());

        ANSWERING_RECIPE_SERIALIZER = SERIALIZER.register("answering_recipe",
                AnsweringRecipe.Serializer::new);
        ANSWERING_RECIPE_TYPE = RECIPE_TYPE.register("answering_recipe",
                () -> new RecipeType<>() {
                    @Override
                    public String toString() {
                        return "answering_recipe";
                    }
                });

        CREATIVE_MODE_TABS.register("wenyan_nature", () -> CreativeModeTab.builder()
                .title(Component.translatable("title.wenyan_nature.create_tab"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> HAND_RUNNER_1.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(HAND_RUNNER.get());
                    output.accept(HAND_RUNNER_1.get());
                    output.accept(HAND_RUNNER_2.get());
                    output.accept(HAND_RUNNER_3.get());
                }).build());
    }
}
