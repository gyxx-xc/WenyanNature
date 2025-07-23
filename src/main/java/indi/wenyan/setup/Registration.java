package indi.wenyan.setup;

import com.mojang.datafixers.DSL;
import indi.wenyan.content.block.*;
import indi.wenyan.content.block.additional_module.ExplosiveAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.ExplosiveAdditionalModuleEntity;
import indi.wenyan.content.block.additional_module.InformativeAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.InformativeAdditionalModuleEntity;
import indi.wenyan.content.block.additional_module.InteractiveAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.InteractiveAdditionalModuleEntity;
import indi.wenyan.content.block.additional_module.MathAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.MathAdditionalModuleEntity;
import indi.wenyan.content.block.additional_module.BitAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.BitAdditionalModuleEntity;
import indi.wenyan.content.block.additional_module.RandomAdditionalModuleBlock;
import indi.wenyan.content.block.additional_module.RandomAdditionalModuleEntity;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.content.data.OutputData;
import indi.wenyan.content.data.ProgramCodeData;
import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.item.WenyanHandRunner;
import indi.wenyan.content.item.ink.*;
import indi.wenyan.content.item.paper.*;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.setup.network.BlockOutputPacket;
import indi.wenyan.setup.network.BlockRunnerCodePacket;
import indi.wenyan.setup.network.CommunicationLocationPacket;
import indi.wenyan.setup.network.RunnerCodePacket;
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

import static indi.wenyan.WenyanProgramming.MODID;

public final class Registration {

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        ENTITY.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENU_TYPE.register(modEventBus);
        DATA.register(modEventBus);
        RECIPE_TYPE.register(modEventBus);
        SERIALIZER.register(modEventBus);

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

    public static final DeferredItem<Item> BAMBOO_PAPER;
    public static final DeferredItem<Item> CLOUD_PAPER;
    public static final DeferredItem<Item> DRAGON_PAPER;
    public static final DeferredItem<Item> FROST_PAPER;
    public static final DeferredItem<Item> PHOENIX_PAPER;
    public static final DeferredItem<Item> STAR_PAPER;

    public static final DeferredItem<Item> ARCANE_INK;
    public static final DeferredItem<Item> BAMBOO_INK;
    public static final DeferredItem<Item> CELESTIAL_INK;
    public static final DeferredItem<Item> LUNAR_INK;
    public static final DeferredItem<Item> CINNABAR_INK;
    public static final DeferredItem<Item> STARLIGHT_INK;

    public static final DeferredBlock<RunnerBlock> RUNNER_BLOCK;
    public static final Supplier<BlockEntityType<RunnerBlockEntity>> RUNNER_BLOCK_ENTITY;
    public static final DeferredBlock<AdditionalPaper> ADDITIONAL_PAPER_BLOCK;
    public static final DeferredItem<BlockItem> ADDITIONAL_PAPER_BLOCK_ITEM;

    public static final DeferredBlock<CraftingBlock> CRAFTING_BLOCK;
    public static final DeferredItem<BlockItem> CRAFTING_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<CraftingBlockEntity>> CRAFTING_ENTITY;
    public static final DeferredBlock<PedestalBlock> PEDESTAL_BLOCK;
    public static final DeferredItem<BlockItem> PEDESTAL_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY;
    public static final DeferredBlock<ExplosiveAdditionalModuleBlock> EXPLOSIVE_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> EXPLOSIVE_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<ExplosiveAdditionalModuleEntity>> EXPLOSIVE_MODULE_ENTITY;
    public static final DeferredBlock<InformativeAdditionalModuleBlock> INFORMATIVE_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> INFORMATIVE_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<InformativeAdditionalModuleEntity>> INFORMATIVE_MODULE_ENTITY;
    public static final DeferredBlock<InteractiveAdditionalModuleBlock> INTERACTIVE_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> INTERACTIVE_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<InteractiveAdditionalModuleEntity>> INTERACTIVE_MODULE_ENTITY;
    public static final DeferredBlock<MathAdditionalModuleBlock> MATH_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> MATH_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<MathAdditionalModuleEntity>> MATH_MODULE_ENTITY;
    public static final DeferredBlock<BitAdditionalModuleBlock> BIT_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> BIT_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<BitAdditionalModuleEntity>> BIT_MODULE_ENTITY;
    public static final DeferredBlock<RandomAdditionalModuleBlock> RANDOM_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> RANDOM_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<RandomAdditionalModuleEntity>> RANDOM_MODULE_ENTITY;


    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY;
    public static final Supplier<EntityType<BulletEntity>> BULLET_ENTITY;

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RunnerTierData>> TIER_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OutputData>> OUTPUT_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ProgramCodeData>> PROGRAM_CODE_DATA;

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnsweringRecipe>> ANSWERING_RECIPE_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnsweringRecipe>> ANSWERING_RECIPE_TYPE;

    private static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(
                RunnerCodePacket.TYPE,
                RunnerCodePacket.STREAM_CODEC,
                RunnerCodePacket.HANDLER);
        registrar.playToServer(
                BlockRunnerCodePacket.TYPE,
                BlockRunnerCodePacket.STREAM_CODEC,
                BlockRunnerCodePacket.HANDLER);
        registrar.commonToClient(
                BlockOutputPacket.TYPE,
                BlockOutputPacket.STREAM_CODEC,
                BlockOutputPacket.HANDLER);
        registrar.commonToClient(
                CommunicationLocationPacket.TYPE,
                CommunicationLocationPacket.STREAM_CODEC,
                CommunicationLocationPacket.HANDLER);
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

        HAND_RUNNER = ITEMS.registerItem(HandRunnerEntity.ID_0,
                (Item.Properties properties) -> new WenyanHandRunner(properties, 0));
        HAND_RUNNER_1 = ITEMS.registerItem(HandRunnerEntity.ID_1,
                (Item.Properties properties) -> new WenyanHandRunner(properties, 1));
        HAND_RUNNER_2 = ITEMS.registerItem(HandRunnerEntity.ID_2,
                (Item.Properties properties) -> new WenyanHandRunner(properties, 2));
        HAND_RUNNER_3 = ITEMS.registerItem(HandRunnerEntity.ID_3,
                (Item.Properties properties) -> new WenyanHandRunner(properties, 3));

        RUNNER_BLOCK = BLOCKS.register(RunnerBlock.ID, RunnerBlock::new);
        RUNNER_BLOCK_ENTITY = BLOCK_ENTITY.register(RunnerBlock.ID,
                () -> BlockEntityType.Builder
                        .of(RunnerBlockEntity::new, RUNNER_BLOCK.get())
                        .build(DSL.remainderType()));
        HAND_RUNNER_ENTITY = ENTITY.register(HandRunnerEntity.ID_1,
                () -> EntityType.Builder
                        .of((EntityType.EntityFactory<HandRunnerEntity>) HandRunnerEntity::new, MobCategory.MISC)
                        .sized(0.45f, 1.0f)
                        .build(HandRunnerEntity.ID_1));

        // Paper
        BAMBOO_PAPER = ITEMS.registerItem(BambooPaper.ID, BambooPaper::new);
        CLOUD_PAPER = ITEMS.registerItem(CloudPaper.ID, CloudPaper::new);
        DRAGON_PAPER = ITEMS.registerItem(DragonPaper.ID, DragonPaper::new);
        FROST_PAPER = ITEMS.registerItem(FrostPaper.ID, FrostPaper::new);
        PHOENIX_PAPER = ITEMS.registerItem(PhoenixPaper.ID, PhoenixPaper::new);
        STAR_PAPER = ITEMS.registerItem(StarPaper.ID, StarPaper::new);

        // Ink
        ARCANE_INK = ITEMS.registerItem(ArcaneInk.ID, ArcaneInk::new);
        BAMBOO_INK = ITEMS.registerItem(BambooInk.ID, BambooInk::new);
        CELESTIAL_INK = ITEMS.registerItem(CelestialInk.ID, CelestialInk::new);
        LUNAR_INK = ITEMS.registerItem(LunarInk.ID, LunarInk::new);
        CINNABAR_INK = ITEMS.registerItem(CinnabarInk.ID, CinnabarInk::new);
        STARLIGHT_INK = ITEMS.registerItem(StarlightInk.ID, StarlightInk::new);

        BULLET_ENTITY = ENTITY.register(BulletEntity.ID,
                () -> EntityType.Builder
                        .of((EntityType.EntityFactory<BulletEntity>) BulletEntity::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .build(BulletEntity.ID));

        CRAFTING_BLOCK = BLOCKS.register(CraftingBlock.ID, CraftingBlock::new);
        CRAFTING_BLOCK_ITEM = ITEMS.registerItem(CraftingBlock.ID,
                (properties) -> new BlockItem(CRAFTING_BLOCK.get(), properties));
        CRAFTING_ENTITY = BLOCK_ENTITY.register(CraftingBlock.ID,
                () -> BlockEntityType.Builder
                        .of(CraftingBlockEntity::new, CRAFTING_BLOCK.get())
                        .build(DSL.remainderType()));
        CRAFTING_CONTAINER = MENU_TYPE.register(CraftingBlock.ID,
                () -> IMenuTypeExtension.create(CraftingBlockContainer::new));

        PEDESTAL_BLOCK = BLOCKS.register(PedestalBlock.ID, PedestalBlock::new);
        PEDESTAL_BLOCK_ITEM = ITEMS.registerItem(PedestalBlock.ID,
                (properties) -> new BlockItem(PEDESTAL_BLOCK.get(), properties));
        PEDESTAL_ENTITY = BLOCK_ENTITY.register(PedestalBlock.ID,
                () -> BlockEntityType.Builder
                        .of(PedestalBlockEntity::new, PEDESTAL_BLOCK.get())
                        .build(DSL.remainderType()));

        EXPLOSIVE_MODULE_BLOCK = BLOCKS.register(ExplosiveAdditionalModuleBlock.ID, ExplosiveAdditionalModuleBlock::new);
        EXPLOSIVE_MODULE_BLOCK_ITEM = ITEMS.registerItem(ExplosiveAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(EXPLOSIVE_MODULE_BLOCK.get(), properties));
        EXPLOSIVE_MODULE_ENTITY = BLOCK_ENTITY.register(ExplosiveAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(ExplosiveAdditionalModuleEntity::new, EXPLOSIVE_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        INFORMATIVE_MODULE_BLOCK = BLOCKS.register(InformativeAdditionalModuleBlock.ID,
                InformativeAdditionalModuleBlock::new);
        INFORMATIVE_MODULE_BLOCK_ITEM = ITEMS.registerItem(InformativeAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(INFORMATIVE_MODULE_BLOCK.get(), properties));
        INFORMATIVE_MODULE_ENTITY = BLOCK_ENTITY.register(InformativeAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(InformativeAdditionalModuleEntity::new, INFORMATIVE_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        INTERACTIVE_MODULE_BLOCK = BLOCKS.register(InteractiveAdditionalModuleBlock.ID, InteractiveAdditionalModuleBlock::new);
        INTERACTIVE_MODULE_BLOCK_ITEM = ITEMS.registerItem(InteractiveAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(INTERACTIVE_MODULE_BLOCK.get(), properties));
        INTERACTIVE_MODULE_ENTITY = BLOCK_ENTITY.register(InteractiveAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(InteractiveAdditionalModuleEntity::new, INTERACTIVE_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));
        MATH_MODULE_BLOCK = BLOCKS.register(MathAdditionalModuleBlock.ID, MathAdditionalModuleBlock::new);
        MATH_MODULE_BLOCK_ITEM = ITEMS.registerItem(MathAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(MATH_MODULE_BLOCK.get(), properties));
        MATH_MODULE_ENTITY = BLOCK_ENTITY.register(MathAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(MathAdditionalModuleEntity::new, MATH_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));
        BIT_MODULE_BLOCK = BLOCKS.register(BitAdditionalModuleBlock.ID, BitAdditionalModuleBlock::new);
        BIT_MODULE_BLOCK_ITEM = ITEMS.registerItem(BitAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(BIT_MODULE_BLOCK.get(), properties));
        BIT_MODULE_ENTITY = BLOCK_ENTITY.register(BitAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(BitAdditionalModuleEntity::new, BIT_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        RANDOM_MODULE_BLOCK = BLOCKS.register(RandomAdditionalModuleBlock.ID, RandomAdditionalModuleBlock::new);
        RANDOM_MODULE_BLOCK_ITEM = ITEMS.registerItem(RandomAdditionalModuleBlock.ID,
                (properties) -> new BlockItem(RANDOM_MODULE_BLOCK.get(), properties));
        RANDOM_MODULE_ENTITY = BLOCK_ENTITY.register(RandomAdditionalModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(RandomAdditionalModuleEntity::new, RANDOM_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));


        ADDITIONAL_PAPER_BLOCK = BLOCKS.register(AdditionalPaper.ID, AdditionalPaper::new);
        ADDITIONAL_PAPER_BLOCK_ITEM = ITEMS.registerItem(AdditionalPaper.ID,
                (properties) -> new BlockItem(ADDITIONAL_PAPER_BLOCK.get(), properties));

        TIER_DATA = DATA.register(RunnerTierData.ID,
                () -> DataComponentType.<RunnerTierData>builder()
                        .persistent(RunnerTierData.CODEC)
                        .build());
        OUTPUT_DATA = DATA.register(OutputData.ID,
                () -> DataComponentType.<OutputData>builder()
                        .persistent(OutputData.CODEC)
                        .networkSynchronized(OutputData.STREAM_CODEC)
                        .build());
        PROGRAM_CODE_DATA = DATA.register(ProgramCodeData.ID,
                () -> DataComponentType.<ProgramCodeData>builder()
                        .persistent(ProgramCodeData.CODEC)
                        .networkSynchronized(ProgramCodeData.STREAM_CODEC)
                        .build());

        ANSWERING_RECIPE_SERIALIZER = SERIALIZER.register(AnsweringRecipe.ID,
                AnsweringRecipe.Serializer::new);
        ANSWERING_RECIPE_TYPE = RECIPE_TYPE.register(AnsweringRecipe.ID,
                () -> new RecipeType<>() {
                    @Override
                    public String toString() {
                        return AnsweringRecipe.ID;
                    }
                });

        CREATIVE_MODE_TABS.register("wenyan_programming", () -> CreativeModeTab.builder()
                .title(Component.translatable("title.wenyan_programming.create_tab"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> HAND_RUNNER_1.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(HAND_RUNNER.get());
                    output.accept(HAND_RUNNER_1.get());
                    output.accept(HAND_RUNNER_2.get());
                    output.accept(HAND_RUNNER_3.get());

                    output.accept(BAMBOO_PAPER.get());
                    output.accept(CLOUD_PAPER.get());
                    output.accept(DRAGON_PAPER.get());
                    output.accept(FROST_PAPER.get());
                    output.accept(PHOENIX_PAPER.get());
                    output.accept(STAR_PAPER.get());

                    output.accept(ARCANE_INK.get());
                    output.accept(BAMBOO_INK.get());
                    output.accept(CELESTIAL_INK.get());
                    output.accept(LUNAR_INK.get());
                    output.accept(CINNABAR_INK.get());
                    output.accept(STARLIGHT_INK.get());

                    output.accept(ADDITIONAL_PAPER_BLOCK_ITEM.get());
                    output.accept(CRAFTING_BLOCK_ITEM.get());
                    output.accept(PEDESTAL_BLOCK_ITEM.get());
                    output.accept(EXPLOSIVE_MODULE_BLOCK_ITEM.get());
                    output.accept(INFORMATIVE_MODULE_BLOCK_ITEM.get());
                    output.accept(INTERACTIVE_MODULE_BLOCK_ITEM.get());
                    output.accept(MATH_MODULE_BLOCK_ITEM.get());
                    output.accept(BIT_MODULE_BLOCK_ITEM.get());
                    output.accept(RANDOM_MODULE_BLOCK_ITEM.get());
                }).build());
    }
}
