package indi.wenyan.setup.definitions;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanProgramming.MODID;

public enum WenyanVillageTrade {
    ;

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
        TRADE_SETS.register(modEventBus);
    }

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MODID);
    public static final DeferredRegister<TradeSet> TRADE_SETS = DeferredRegister.create(Registries.TRADE_SET, MODID);

//    public static final DeferredHolder<TradeSet, TradeSet> WRITING_TRADE_SET = TRADE_SETS.register("writing_trade_set", () -> new TradeSet(
//            context.lookup(Registries.VILLAGER_TRADE).getOrThrow(tradeTag),
//            ConstantValue.exactly(2.0F),
//            false,
//            Optional.of(resourceKey.identifier().withPrefix("trade_set/"))));
    public static final DeferredHolder<PoiType, PoiType> WRITING_POI = POI_TYPES.register("writing_poi", () -> new PoiType(
            ImmutableSet.copyOf(WenyanBlocks.WRITING_BLOCK.get().getStateDefinition().getPossibleStates()),
            1, 1));
    public static final Supplier<VillagerProfession> PROGRAMMER_PROFESSION = VILLAGER_PROFESSIONS.register("programmer", () -> new VillagerProfession(
            Component.literal("programmer"), // TODO: Localize
            entry -> entry.is(WRITING_POI.getKey()),
            entry -> entry.is(WRITING_POI.getKey()),
            ImmutableSet.of(),
            ImmutableSet.of(),
            SoundEvents.VILLAGER_WORK_LIBRARIAN,
            Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, TradeSets.LIBRARIAN_LEVEL_1),
                    Int2ObjectMap.entry(2, TradeSets.CLERIC_LEVEL_2),
                    Int2ObjectMap.entry(3, TradeSets.CLERIC_LEVEL_3),
                    Int2ObjectMap.entry(4, TradeSets.CLERIC_LEVEL_4),
                    Int2ObjectMap.entry(5, TradeSets.CLERIC_LEVEL_5)
            )));
}
