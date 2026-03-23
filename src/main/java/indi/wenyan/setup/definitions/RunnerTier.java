package indi.wenyan.setup.definitions;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RunnerTier {
    RUNNER_0(1, "_0"),
    RUNNER_1(10, "_1"),
    RUNNER_2(100, "_2"),
    RUNNER_3(1000, "_3"),
    RUNNER_4(10000, "_4"),
    RUNNER_5(100000, "_5"),
    RUNNER_6(1000000, "_6");

    public static final Codec<RunnerTier> CODEC = Codec.STRING.xmap(RunnerTier::valueOf, RunnerTier::name);

    @Getter
    private final int stepSpeed;

    private final String tierId;

    RunnerTier(int stepSpeed, String tierId) {
        this.stepSpeed = stepSpeed;
        this.tierId = tierId;
    }

    public String getTieredName(String baseName) {
        return baseName + tierId;
    }

    public static class TieredBlockRegistrator<T extends Block> {
        private final Map<RunnerTier, DeferredBlock<T>> blockMap = new HashMap<>();

        public static <T extends Block> TieredBlockRegistrator<T> registerTieredBlock(String baseName, TieredBlockProvider<T> provider) {
            TieredBlockRegistrator<T> blocks = new TieredBlockRegistrator<>();
            for (RunnerTier tier : values()) {
                blocks.blockMap.put(tier, WenyanBlocks.DR.registerBlock(tier.getTieredName(baseName),
                        properties -> provider.provide(tier, properties)));
            }
            return blocks;
        }

        public T getBlock(RunnerTier tier) {
            return blockMap.get(tier).get();
        }

        public List<T> getBlocks() {
            return blockMap.values().stream().map(DeferredBlock::get).toList();
        }

        @FunctionalInterface
        public interface TieredBlockProvider<T extends Block> {
            T provide(RunnerTier tier, Block.Properties properties);
        }
    }

    public static class TieredItemRegistrator<T extends Item> {
        private final Map<RunnerTier, DeferredItem<T>> itemMap = new HashMap<>();

        public static <T extends Item> TieredItemRegistrator<T> registerTieredItem(String baseName, TieredItemProvider<T> provider) {
            TieredItemRegistrator<T> items = new TieredItemRegistrator<>();
            for (RunnerTier tier : values()) {
                items.itemMap.put(tier, WenyanItems.DR.registerItem(tier.getTieredName(baseName),
                        properties -> provider.provide(tier, properties)));
            }
            return items;
        }

        public T getItem(RunnerTier tier) {
            return itemMap.get(tier).get();
        }

        public List<T> getItems() {
            return itemMap.values().stream().map(DeferredItem::get).toList();
        }

        public List<T> getItemsSorted() {
            return itemMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .map(DeferredItem::get).toList();
        }

        @FunctionalInterface
        public interface TieredItemProvider<T extends Item> {
            T provide(RunnerTier tier, Item.Properties properties);
        }
    }
}
