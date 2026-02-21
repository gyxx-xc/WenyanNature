package indi.wenyan.setup;

import com.mojang.serialization.Codec;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.setup.definitions.WenyanItems;
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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Central registration class for all mod content
 */
public final class Registration {
    private Registration() {}

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
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY;
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS;
    public static final DeferredRegister<EntityType<?>> ENTITY;
    public static final DeferredRegister<MenuType<?>> MENU_TYPE;
    public static final DeferredRegister<DataComponentType<?>> DATA;
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER;
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE;
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    //    public static final Supplier<EntityType<HandRunnerEntity>> HAND_RUNNER_ENTITY;
//    public static final Supplier<EntityType<BulletEntity>> BULLET_ENTITY;

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER;

    public static final Supplier<SimpleParticleType> COMMUNICATION_PARTICLES;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RUNNING_TIER_DATA;
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
        registrar.playToServer(RunnerTitlePacket.TYPE,
                RunnerTitlePacket.STREAM_CODEC,
                RunnerTitlePacket.HANDLER);
        registrar.playToServer(PlatformRenamePacket.TYPE,
                PlatformRenamePacket.STREAM_CODEC,
                PlatformRenamePacket.HANDLER);
        registrar.commonToClient(BlockOutputPacket.TYPE,
                BlockOutputPacket.STREAM_CODEC,
                BlockOutputPacket.HANDLER);
        registrar.commonToClient(CommunicationLocationPacket.TYPE,
                CommunicationLocationPacket.STREAM_CODEC,
                CommunicationLocationPacket.HANDLER);
        registrar.commonToClient(CraftClearParticlePacket.TYPE,
                CraftClearParticlePacket.STREAM_CODEC,
                CraftClearParticlePacket.HANDLER);
        registrar.commonToClient(BlockPosRangePacket.TYPE,
                BlockPosRangePacket.STREAM_CODEC,
                BlockPosRangePacket.HANDLER);
        registrar.commonToClient(CraftingParticlePacket.TYPE,
                CraftingParticlePacket.STREAM_CODEC,
                CraftingParticlePacket.HANDLER);
        registrar.commonToClient(PlatformOutputPacket.TYPE,
                PlatformOutputPacket.STREAM_CODEC,
                PlatformOutputPacket.HANDLER);
    }

    // Static initialization block
    static {
        CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
        ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
        BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
        MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
        BLOCKS = DeferredRegister.createBlocks(MODID);
        DATA = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
        SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
        RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
        PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, WenyanProgramming.MODID);

        CRAFTING_CONTAINER = MENU_TYPE.register(CraftingBlock.ID,
                () -> IMenuTypeExtension.create(CraftingBlockContainer::new));

        RUNNING_TIER_DATA = DATA.register("runner_tier_data",
                () -> DataComponentType.<Integer>builder()
                        .persistent(ExtraCodecs.intRange(0, 3))
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
                .icon(() -> WenyanItems.HAND_RUNNER_1.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(WenyanItems.HAND_RUNNER_0.get());
                    output.accept(WenyanItems.HAND_RUNNER_1.get());
                    output.accept(WenyanItems.HAND_RUNNER_2.get());
                    output.accept(WenyanItems.HAND_RUNNER_3.get());

                    output.accept(WenyanItems.BAMBOO_PAPER.get());
                    output.accept(WenyanItems.CLOUD_PAPER.get());
                    output.accept(WenyanItems.STAR_PAPER.get());
                    output.accept(WenyanItems.FROST_PAPER.get());
                    output.accept(WenyanItems.PHOENIX_PAPER.get());
                    output.accept(WenyanItems.DRAGON_PAPER.get());


                    output.accept(WenyanItems.BAMBOO_INK.get());
                    output.accept(WenyanItems.CINNABAR_INK.get());
                    output.accept(WenyanItems.STARLIGHT_INK.get());
                    output.accept(WenyanItems.LUNAR_INK.get());
                    output.accept(WenyanItems.CELESTIAL_INK.get());
                    output.accept(WenyanItems.ARCANE_INK.get());


                    output.accept(WenyanItems.EQUIPABLE_RUNNER_ITEM.get());
                    output.accept(WenyanItems.PRINT_INVENTORY_MODULE.get());

                    output.accept(WenyanItems.FLOAT_NOTE.get());
                    output.accept(WenyanItems.CRAFTING_BLOCK_ITEM.get());
                    output.accept(WenyanItems.PEDESTAL_BLOCK_ITEM.get());

                    output.accept(WenyanItems.BIT_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.MATH_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.VEC3_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.STRING_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get());

                    output.accept(WenyanItems.ITEM_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get());

                    output.accept(WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get());

                    output.accept(WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.LOCK_MODULE_BLOCK_ITEM.get());
                    output.accept(WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get());

                    output.accept(WenyanItems.POWER_BLOCK_ITEM.get());
                }).build());
    }
}
