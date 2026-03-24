package indi.wenyan.setup.definitions;

import com.mojang.serialization.Codec;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.runner.ICodeHolder;
import indi.wenyan.content.gui_api.CraftingBlockContainer;
import indi.wenyan.content.item.ink.BambooInk;
import indi.wenyan.content.item.throw_runner.FuContainerComponent;
import indi.wenyan.content.recipe.answering.AnsweringRecipe;
import indi.wenyan.content.recipe.combine_module.ThrowModuleRecipe;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.IWenyanDevice;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Central registration class for all mod content
 */
public enum WyRegistration {
    ;


    /**
     * Registers all content with the mod event bus
     */
    public static void register(IEventBus modEventBus) {
        MENU_TYPE.register(modEventBus);
        DATA.register(modEventBus);
        RECIPE_TYPE.register(modEventBus);
        RECIPE_SERIALIZER.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        RECIPE_BOOK_CATEGORIES.register(modEventBus);
    }

    public static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, WenyanProgramming.MODID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, MODID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECT = DeferredRegister.create(BuiltInRegistries.CONSUME_EFFECT_TYPE, MODID);

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER = MENU_TYPE.register(CraftingBlock.ID,
            () -> IMenuTypeExtension.create(CraftingBlockContainer::new));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PROGRAM_CODE_DATA = DATA.register("program_code_data",
            () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NOTE_LOCK_DATA = DATA.register("note_lock_data",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FuContainerComponent>> FU_DATA = DATA.register(FuContainerComponent.ID,
            () -> DataComponentType.<FuContainerComponent>builder()
                    .persistent(FuContainerComponent.CODEC)
                    .networkSynchronized(FuContainerComponent.STREAM_CODEC)
                    .build());

    public static final Supplier<RecipeBookCategory> CALCULATION_BLOCK_CATEGORY = RECIPE_BOOK_CATEGORIES.register(
            "calculation_block", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnsweringRecipe>> ANSWERING_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register(AnsweringRecipe.ID,
            AnsweringRecipe.SerializerProvider::create);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnsweringRecipe>> ANSWERING_RECIPE_TYPE = RECIPE_TYPE.register(AnsweringRecipe.ID,
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return AnsweringRecipe.ID;
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ThrowModuleRecipe>> THROW_MODULE_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register(ThrowModuleRecipe.ID,
            () -> ThrowModuleRecipe.SERIALIZER);

    public static final BlockCapability<IWenyanBlockDevice, Void> WENYAN_BLOCK_DEVICE_CAPABILITY = BlockCapability.createVoid(
            Identifier.fromNamespaceAndPath(MODID, "wenyan_block_device"),
            IWenyanBlockDevice.class);
    public static final ItemCapability<ICodeHolder, Void> ITEM_CODE_HOLDER_CAPABILITY = ItemCapability.createVoid(
            Identifier.fromNamespaceAndPath(MODID, "item_code_holder"),
            ICodeHolder.class);
    public static final ItemCapability<IWenyanDevice, Void> WENYAN_ITEM_DEVICE_CAPABILITY = ItemCapability.createVoid(
            Identifier.fromNamespaceAndPath(MODID, "wenyan_item_device"),
            IWenyanDevice.class);

    public static final TagKey<Item> MODULE_ITEM = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(MODID, "module_item")
    );

    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<BambooInk.NoFireEffect>> NO_FIRE_EFFECT = CONSUME_EFFECT.register("no_fire_effect",
            () -> new ConsumeEffect.Type<>(BambooInk.NoFireEffect.CODEC, BambooInk.NoFireEffect.STREAM_CODEC));
}
