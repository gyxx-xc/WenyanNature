package indi.wenyan.setup.datagen.loot;

import com.google.common.collect.ImmutableMap;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BlockDropProvider extends BlockLootSubProvider {
    private final Map<Block, Function<Block, LootTable.Builder>> overrides = createOverrides();

    @NotNull
    private ImmutableMap<Block, Function<Block, LootTable.Builder>> createOverrides() {
        return ImmutableMap.<Block, Function<Block, LootTable.Builder>>builder()
                .putAll(() -> WenyanBlocks.RUNNER_BLOCK.getBlocks().stream().<Map.Entry<? extends Block, ? extends Function<Block, LootTable.Builder>>>map(b ->
                        Map.entry(b, this::withComponent)
                ).iterator())
                .build();
    }

    public BlockDropProvider(HolderLookup.Provider providers) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), providers);
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK
                .stream()
                .filter(entry -> {
                    var lootTable = entry.getLootTable().orElse(null);
                    return lootTable != null && lootTable.identifier().getNamespace().equals(WenyanProgramming.MODID);
                })
                .toList();
    }

    @Override
    public void generate() {
        for (var block : getKnownBlocks()) {
            add(block, overrides.getOrDefault(block, this::defaultBuilder).apply(block));
        }
    }

    private LootTable.Builder defaultBuilder(Block block) {
        if (block.asItem() == Items.AIR) return LootTable.lootTable();
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(block);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
                .when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(pool);
    }

    private LootTable.Builder withComponent(Block block) {
        if (block.asItem() == Items.AIR) return LootTable.lootTable();
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(block);
        LootPool.Builder pool = LootPool.lootPool()
                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))
                .setRolls(ConstantValue.exactly(1)).add(entry)
                .when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(pool);
    }

}
