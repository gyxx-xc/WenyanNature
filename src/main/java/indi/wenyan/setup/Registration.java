package indi.wenyan.setup;

import com.mojang.datafixers.DSL;
import com.mojang.serialization.Codec;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlock;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlockEntity;
import indi.wenyan.content.block.additional_module.block.SemaphoreModuleBlock;
import indi.wenyan.content.block.additional_module.block.SemaphoreModuleEntity;
import indi.wenyan.content.block.additional_module.builtin.*;
import indi.wenyan.content.block.additional_module.paper.*;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.crafting_block.CraftingBlockEntity;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.item.FloatNoteItem;
import indi.wenyan.content.item.WenyanHandRunner;
import indi.wenyan.content.item.ink.*;
import indi.wenyan.content.item.paper.*;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.setup.network.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
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

/**
 * Central registration class for all mod content
 */
public final class Registration {

    /**
     * Registers all content with the mod event bus
     */
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
        PARTICLE_TYPES.register(modEventBus);

        modEventBus.addListener(Registration::onRegisterPayloadHandler);
    }

    // Registry objects
    public static final DeferredRegister.Blocks BLOCKS;
    public static final DeferredRegister.Items ITEMS;
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY;
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS;
    public static final DeferredRegister<EntityType<?>> ENTITY;
    public static final DeferredRegister<MenuType<?>> MENU_TYPE;
    public static final DeferredRegister<DataComponentType<?>> DATA;
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER;
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE;
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    // Hand Runner items
    public static final DeferredItem<Item> HAND_RUNNER_0;
    public static final DeferredItem<Item> HAND_RUNNER_1;
    public static final DeferredItem<Item> HAND_RUNNER_2;
    public static final DeferredItem<Item> HAND_RUNNER_3;

    public static final DeferredItem<Item> FLOAT_NOTE;

    // Paper items
    public static final DeferredItem<Item> BAMBOO_PAPER;
    public static final DeferredItem<Item> CLOUD_PAPER;
    public static final DeferredItem<Item> DRAGON_PAPER;
    public static final DeferredItem<Item> FROST_PAPER;
    public static final DeferredItem<Item> PHOENIX_PAPER;
    public static final DeferredItem<Item> STAR_PAPER;

    // Ink items
    public static final DeferredItem<Item> ARCANE_INK;
    public static final DeferredItem<Item> BAMBOO_INK;
    public static final DeferredItem<Item> CELESTIAL_INK;
    public static final DeferredItem<Item> LUNAR_INK;
    public static final DeferredItem<Item> CINNABAR_INK;
    public static final DeferredItem<Item> STARLIGHT_INK;

    public static final DeferredBlock<RunnerBlock> RUNNER_BLOCK;
    public static final Supplier<BlockEntityType<RunnerBlockEntity>> RUNNER_BLOCK_ENTITY;
//    public static final DeferredBlock<AdditionalPaper> ADDITIONAL_PAPER_BLOCK;
//    public static final DeferredItem<BlockItem> ADDITIONAL_PAPER_BLOCK_ITEM;

    public static final DeferredBlock<CraftingBlock> CRAFTING_BLOCK;
    public static final DeferredItem<BlockItem> CRAFTING_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<CraftingBlockEntity>> CRAFTING_ENTITY;
    public static final DeferredBlock<PedestalBlock> PEDESTAL_BLOCK;
    public static final DeferredItem<BlockItem> PEDESTAL_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY;
    public static final DeferredBlock<ExplosionModuleBlock> EXPLOSION_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> EXPLOSION_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<ExplosionModuleEntity>> EXPLOSION_MODULE_ENTITY;
    public static final DeferredBlock<WorldModuleBlock> INFORMATION_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> INFORMATION_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<WorldModuleEntity>> INFORMATION_MODULE_ENTITY;
    public static final DeferredBlock<MathModuleBlock> MATH_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> MATH_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<MathModuleEntity>> MATH_MODULE_ENTITY;
    public static final DeferredBlock<BitModuleBlock> BIT_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> BIT_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<BitModuleEntity>> BIT_MODULE_ENTITY;
    public static final DeferredBlock<BlockModuleBlock> BLOCK_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> BLOCK_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<BlockModuleEntity>> BLOCK_MODULE_ENTITY;
    public static final DeferredBlock<RandomModuleBlock> RANDOM_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> RANDOM_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<RandomModuleEntity>> RANDOM_MODULE_ENTITY;
    public static final DeferredBlock<ItemModuleBlock> ITEM_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> ITEM_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<ItemModuleEntity>> ITEM_MODULE_ENTITY;
    public static final DeferredBlock<Vec3ModuleBlock> VEC3_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> VEC3_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<Vec3ModuleEntity>> VEC3_MODULE_ENTITY;

    public static final DeferredBlock<ScreenModuleBlock> SCREEN_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> SCREEN_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<ScreenModuleBlockEntity>> SCREEN_MODULE_BLOCK_ENTITY;

    public static final DeferredBlock<CommunicateModuleBlock> COMMUNICATE_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> COMMUNICATE_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<CommunicateModuleEntity>> COMMUNICATE_MODULE_ENTITY;

    public static final DeferredBlock<SemaphoreModuleBlock> SEMAPHORE_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> SEMAPHORE_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<SemaphoreModuleEntity>> SEMAPHORE_MODULE_ENTITY;

    public static final DeferredBlock<CollectionModuleBlock> COLLECTION_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> COLLECTION_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<CollectionModuleEntity>> COLLECTION_MODULE_ENTITY;

    public static final DeferredBlock<StringModuleBlock> STRING_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> STRING_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<StringModuleEntity>> STRING_MODULE_ENTITY;

    public static final DeferredBlock<EntityModuleBlock> ENTITY_MODULE_BLOCK;
    public static final DeferredItem<BlockItem> ENTITY_MODULE_BLOCK_ITEM;
    public static final Supplier<BlockEntityType<EntityModuleEntity>> ENTITY_MODULE_ENTITY;

    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY;
    public static final Supplier<EntityType<BulletEntity>> BULLET_ENTITY;

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER;

    public static final Supplier<SimpleParticleType> COMMUNICATION_PARTICLES;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RUNNING_TIER_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> OUTPUT_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PROGRAM_CODE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NOTE_LOCK_DATA;

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnsweringRecipe>> ANSWERING_RECIPE_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnsweringRecipe>> ANSWERING_RECIPE_TYPE;

    /**
     * Registers network packet handlers
     */
    private static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(RunnerCodePacket.TYPE,
                RunnerCodePacket.STREAM_CODEC,
                RunnerCodePacket.HANDLER);
        registrar.playToServer(FloatNotePacket.TYPE,
                FloatNotePacket.STREAM_CODEC,
                FloatNotePacket.HANDLER);
        registrar.playToServer(BlockRunnerCodePacket.TYPE,
                BlockRunnerCodePacket.STREAM_CODEC,
                BlockRunnerCodePacket.HANDLER);
        registrar.commonToClient(BlockOutputPacket.TYPE,
                BlockOutputPacket.STREAM_CODEC,
                BlockOutputPacket.HANDLER);
        registrar.commonToClient(CommunicationLocationPacket.TYPE,
                CommunicationLocationPacket.STREAM_CODEC,
                CommunicationLocationPacket.HANDLER);
        registrar.commonToClient(BlockPosRangePacket.TYPE,
                BlockPosRangePacket.STREAM_CODEC,
                BlockPosRangePacket.HANDLER);
    }

    // Static initialization block
    static {
        CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
        ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
        BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
        MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
        ITEMS = DeferredRegister.createItems(MODID);
        BLOCKS = DeferredRegister.createBlocks(MODID);
        DATA = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
        SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
        RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
        PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, WenyanProgramming.MODID);

        HAND_RUNNER_0 = ITEMS.registerItem(HandRunnerEntity.ID_0,
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

        FLOAT_NOTE = ITEMS.registerItem(FloatNoteItem.ID, FloatNoteItem::new);

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

        EXPLOSION_MODULE_BLOCK = BLOCKS.register(ExplosionModuleBlock.ID, ExplosionModuleBlock::new);
        EXPLOSION_MODULE_BLOCK_ITEM = ITEMS.registerItem(ExplosionModuleBlock.ID,
                (properties) -> new BlockItem(EXPLOSION_MODULE_BLOCK.get(), properties));
        EXPLOSION_MODULE_ENTITY = BLOCK_ENTITY.register(ExplosionModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(ExplosionModuleEntity::new, EXPLOSION_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        INFORMATION_MODULE_BLOCK = BLOCKS.register(WorldModuleBlock.ID,
                WorldModuleBlock::new);
        INFORMATION_MODULE_BLOCK_ITEM = ITEMS.registerItem(WorldModuleBlock.ID,
                (properties) -> new BlockItem(INFORMATION_MODULE_BLOCK.get(), properties));
        INFORMATION_MODULE_ENTITY = BLOCK_ENTITY.register(WorldModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(WorldModuleEntity::new, INFORMATION_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        MATH_MODULE_BLOCK = BLOCKS.register(MathModuleBlock.ID, MathModuleBlock::new);
        MATH_MODULE_BLOCK_ITEM = ITEMS.registerItem(MathModuleBlock.ID,
                (properties) -> new BlockItem(MATH_MODULE_BLOCK.get(), properties));
        MATH_MODULE_ENTITY = BLOCK_ENTITY.register(MathModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(MathModuleEntity::new, MATH_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));
        BIT_MODULE_BLOCK = BLOCKS.register(BitModuleBlock.ID, BitModuleBlock::new);
        BIT_MODULE_BLOCK_ITEM = ITEMS.registerItem(BitModuleBlock.ID,
                (properties) -> new BlockItem(BIT_MODULE_BLOCK.get(), properties));
        BIT_MODULE_ENTITY = BLOCK_ENTITY.register(BitModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(BitModuleEntity::new, BIT_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        BLOCK_MODULE_BLOCK = BLOCKS.register(BlockModuleBlock.ID, BlockModuleBlock::new);
        BLOCK_MODULE_BLOCK_ITEM = ITEMS.registerItem(BlockModuleBlock.ID,
                (properties) -> new BlockItem(BLOCK_MODULE_BLOCK.get(), properties));
        BLOCK_MODULE_ENTITY = BLOCK_ENTITY.register(BlockModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(BlockModuleEntity::new, BLOCK_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        RANDOM_MODULE_BLOCK = BLOCKS.register(RandomModuleBlock.ID, RandomModuleBlock::new);
        RANDOM_MODULE_BLOCK_ITEM = ITEMS.registerItem(RandomModuleBlock.ID,
                (properties) -> new BlockItem(RANDOM_MODULE_BLOCK.get(), properties));
        RANDOM_MODULE_ENTITY = BLOCK_ENTITY.register(RandomModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(RandomModuleEntity::new, RANDOM_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        ITEM_MODULE_BLOCK = BLOCKS.register(ItemModuleBlock.ID, ItemModuleBlock::new);
        ITEM_MODULE_BLOCK_ITEM = ITEMS.registerItem(ItemModuleBlock.ID,
                (properties) -> new BlockItem(ITEM_MODULE_BLOCK.get(), properties));
        ITEM_MODULE_ENTITY = BLOCK_ENTITY.register(ItemModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(ItemModuleEntity::new, ITEM_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        VEC3_MODULE_BLOCK = BLOCKS.register(Vec3ModuleBlock.ID, Vec3ModuleBlock::new);
        VEC3_MODULE_BLOCK_ITEM = ITEMS.registerItem(Vec3ModuleBlock.ID,
                (properties) -> new BlockItem(VEC3_MODULE_BLOCK.get(), properties));
        VEC3_MODULE_ENTITY = BLOCK_ENTITY.register(Vec3ModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(Vec3ModuleEntity::new, VEC3_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        SCREEN_MODULE_BLOCK = BLOCKS.register(ScreenModuleBlock.ID, ScreenModuleBlock::new);
        SCREEN_MODULE_BLOCK_ITEM = ITEMS.registerItem(ScreenModuleBlock.ID,
                (properties) -> new BlockItem(SCREEN_MODULE_BLOCK.get(), properties));
        SCREEN_MODULE_BLOCK_ENTITY = BLOCK_ENTITY.register(ScreenModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(ScreenModuleBlockEntity::new, SCREEN_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        COMMUNICATE_MODULE_BLOCK = BLOCKS.register(CommunicateModuleBlock.ID, CommunicateModuleBlock::new);
        COMMUNICATE_MODULE_BLOCK_ITEM = ITEMS.registerItem(CommunicateModuleBlock.ID,
                (properties) -> new BlockItem(COMMUNICATE_MODULE_BLOCK.get(), properties));
        COMMUNICATE_MODULE_ENTITY = BLOCK_ENTITY.register(CommunicateModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(CommunicateModuleEntity::new, COMMUNICATE_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        SEMAPHORE_MODULE_BLOCK = BLOCKS.register(SemaphoreModuleBlock.ID, SemaphoreModuleBlock::new);
        SEMAPHORE_MODULE_BLOCK_ITEM = ITEMS.registerItem(SemaphoreModuleBlock.ID,
                (properties) -> new BlockItem(SEMAPHORE_MODULE_BLOCK.get(), properties));
        SEMAPHORE_MODULE_ENTITY = BLOCK_ENTITY.register(SemaphoreModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(SemaphoreModuleEntity::new, SEMAPHORE_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        COLLECTION_MODULE_BLOCK = BLOCKS.register(CollectionModuleBlock.ID, CollectionModuleBlock::new);
        COLLECTION_MODULE_BLOCK_ITEM = ITEMS.registerItem(CollectionModuleBlock.ID,
                (properties) -> new BlockItem(COLLECTION_MODULE_BLOCK.get(), properties));
        COLLECTION_MODULE_ENTITY = BLOCK_ENTITY.register(CollectionModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(CollectionModuleEntity::new, COLLECTION_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        STRING_MODULE_BLOCK = BLOCKS.register(StringModuleBlock.ID, StringModuleBlock::new);
        STRING_MODULE_BLOCK_ITEM = ITEMS.registerItem(StringModuleBlock.ID,
                (properties) -> new BlockItem(STRING_MODULE_BLOCK.get(), properties));
        STRING_MODULE_ENTITY = BLOCK_ENTITY.register(StringModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(StringModuleEntity::new, STRING_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

        ENTITY_MODULE_BLOCK = BLOCKS.register(EntityModuleBlock.ID, EntityModuleBlock::new);
        ENTITY_MODULE_BLOCK_ITEM = ITEMS.registerItem(EntityModuleBlock.ID,
                (properties) -> new BlockItem(ENTITY_MODULE_BLOCK.get(), properties));
        ENTITY_MODULE_ENTITY = BLOCK_ENTITY.register(EntityModuleBlock.ID,
                () -> BlockEntityType.Builder
                        .of(EntityModuleEntity::new, ENTITY_MODULE_BLOCK.get())
                        .build(DSL.remainderType()));

//        ADDITIONAL_PAPER_BLOCK = BLOCKS.register(AdditionalPaper.ID, AdditionalPaper::new);
//        ADDITIONAL_PAPER_BLOCK_ITEM = ITEMS.registerItem(AdditionalPaper.ID,
//                (properties) -> new BlockItem(ADDITIONAL_PAPER_BLOCK.get(), properties));

        RUNNING_TIER_DATA = DATA.register("runner_tier_data",
                () -> DataComponentType.<Integer>builder()
                        .persistent(ExtraCodecs.intRange(0, 3))
                        .build());
        OUTPUT_DATA = DATA.register("output_data",
                () -> DataComponentType.<String>builder()
                        .persistent(Codec.STRING)
                        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                        .build());
        PROGRAM_CODE_DATA = DATA.register("program_code_data",
                () -> DataComponentType.<String>builder()
                        .persistent(Codec.STRING)
                        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                        .build());
        NOTE_LOCK_DATA = DATA.register("note_lock_data",
                () -> DataComponentType.<Boolean>builder()
                        .persistent(Codec.BOOL)
                        .networkSynchronized(ByteBufCodecs.BOOL)
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

        COMMUNICATION_PARTICLES = PARTICLE_TYPES.register("communication_particles",
                () -> new SimpleParticleType(true));

        CREATIVE_MODE_TABS.register("wenyan_programming", () -> CreativeModeTab.builder()
                .title(Component.translatable("title.wenyan_programming.create_tab"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> HAND_RUNNER_1.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(HAND_RUNNER_0.get());
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

                    output.accept(FLOAT_NOTE.get());
//                    output.accept(ADDITIONAL_PAPER_BLOCK_ITEM.get());
                    output.accept(CRAFTING_BLOCK_ITEM.get());
                    output.accept(PEDESTAL_BLOCK_ITEM.get());

                    output.accept(BIT_MODULE_BLOCK_ITEM.get());
                    output.accept(MATH_MODULE_BLOCK_ITEM.get());
                    output.accept(VEC3_MODULE_BLOCK_ITEM.get());
                    output.accept(RANDOM_MODULE_BLOCK_ITEM.get());
                    output.accept(STRING_MODULE_BLOCK_ITEM.get());
                    output.accept(COLLECTION_MODULE_BLOCK_ITEM.get());

                    output.accept(ITEM_MODULE_BLOCK_ITEM.get());
                    output.accept(BLOCK_MODULE_BLOCK_ITEM.get());
                    output.accept(ENTITY_MODULE_BLOCK_ITEM.get());
                    output.accept(INFORMATION_MODULE_BLOCK_ITEM.get());

                    output.accept(EXPLOSION_MODULE_BLOCK_ITEM.get());

                    output.accept(COMMUNICATE_MODULE_BLOCK_ITEM.get());
                    output.accept(SEMAPHORE_MODULE_BLOCK_ITEM.get());
                    output.accept(SCREEN_MODULE_BLOCK_ITEM.get());
                }).build());
    }
}
