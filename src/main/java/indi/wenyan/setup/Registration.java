package indi.wenyan.setup;

import com.mojang.serialization.Codec;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.content.recipe.AnsweringRecipe;
import indi.wenyan.setup.event.ClientSetup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
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
        ENTITY.register(modEventBus);
        MENU_TYPE.register(modEventBus);
        DATA.register(modEventBus);
        RECIPE_TYPE.register(modEventBus);
        SERIALIZER.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
    }

    // Registry objects
    public static final DeferredRegister<EntityType<?>> ENTITY;
    public static final DeferredRegister<MenuType<?>> MENU_TYPE;
    public static final DeferredRegister<DataComponentType<?>> DATA;
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER;
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE;
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    public static final Supplier<MenuType<CraftingBlockContainer>> CRAFTING_CONTAINER;

    public static final Supplier<SimpleParticleType> COMMUNICATION_PARTICLES;

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RUNNING_TIER_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PROGRAM_CODE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NOTE_LOCK_DATA;

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnsweringRecipe>> ANSWERING_RECIPE_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnsweringRecipe>> ANSWERING_RECIPE_TYPE;

    // Static initialization block
    static {
        ENTITY = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
        MENU_TYPE = DeferredRegister.create(Registries.MENU, MODID);
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
    }
}
