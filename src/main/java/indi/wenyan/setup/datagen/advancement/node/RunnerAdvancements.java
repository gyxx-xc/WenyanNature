package indi.wenyan.setup.datagen.advancement.node;

import indi.wenyan.setup.datagen.advancement.AdvancementUtils;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class RunnerAdvancements implements AdvancementSubProvider {
    private static final Stage[] STAGES = {
            new Stage(
                    RunnerTier.RUNNER_0,
                    new NextTier("bamboo_paper", WenyanItems.BAMBOO_PAPER.get(),
                            "bamboo_ink", WenyanItems.BAMBOO_INK.get(), RunnerTier.RUNNER_1),
                    new ItemLike[0],
                    new ItemLike[] {
                            WenyanItems.CRAFTING_BLOCK_ITEM,
                            WenyanItems.PEDESTAL_BLOCK_ITEM,
                            WenyanItems.WRITING_BLOCK_ITEM,
                            WenyanItems.CREATIVE_POWER_BLOCK_ITEM,
                            WenyanItems.CLOUD_BEACON_BLOCK_ITEM
                    },
                    new ItemLike[] {
                            WenyanItems.FLOAT_NOTE
                    }
            ),
            new Stage(
                    RunnerTier.RUNNER_1,
                    new NextTier("cloud_paper", WenyanItems.CLOUD_PAPER.get(),
                            "cinnabar_ink", WenyanItems.CINNABAR_INK.get(), RunnerTier.RUNNER_2),
                    new ItemLike[] {
                            WenyanItems.BIT_MODULE_BLOCK_ITEM,
                            WenyanItems.MATH_MODULE_BLOCK_ITEM,
                            WenyanItems.VEC3_MODULE_BLOCK_ITEM,
                            WenyanItems.RANDOM_MODULE_BLOCK_ITEM,
                            WenyanItems.STRING_MODULE_BLOCK_ITEM,
                            WenyanItems.COLLECTION_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[] {
                            WenyanItems.LOGIC_FURNACE_BLOCK_ITEM,
                            WenyanItems.SCREEN_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[0]
            ),
            new Stage(
                    RunnerTier.RUNNER_2,
                    new NextTier("starlight_paper", WenyanItems.STARLIGHT_PAPER.get(),
                            "starlight_ink", WenyanItems.STARLIGHT_INK.get(), RunnerTier.RUNNER_3),
                    new ItemLike[] {
                            WenyanItems.ITEM_MODULE_BLOCK_ITEM,
                            WenyanItems.BLOCK_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[0],
                    new ItemLike[0]
            ),
            new Stage(
                    RunnerTier.RUNNER_3,
                    new NextTier("frost_paper", WenyanItems.FROST_PAPER.get(),
                            "lunar_ink", WenyanItems.LUNAR_INK.get(), RunnerTier.RUNNER_4),
                    new ItemLike[] {
                            WenyanItems.ENTITY_MODULE_BLOCK_ITEM,
                            WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[] {
                            WenyanItems.LOCK_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[0]
            ),
            new Stage(
                    RunnerTier.RUNNER_4,
                    new NextTier("phoenix_paper", WenyanItems.PHOENIX_PAPER.get(),
                            "arcane_ink", WenyanItems.ARCANE_INK.get(), RunnerTier.RUNNER_5),
                    new ItemLike[] {
                            WenyanItems.INFORMATION_MODULE_BLOCK_ITEM,
                            WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM,
                            WenyanItems.PISTON_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[] {
                            WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM
                    },
                    new ItemLike[0]
            ),
            new Stage(
                    RunnerTier.RUNNER_5,
                    new NextTier("dragon_paper", WenyanItems.DRAGON_PAPER.get(),
                            "celestial_ink", WenyanItems.CELESTIAL_INK.get(), RunnerTier.RUNNER_6),
                    new ItemLike[0],
                    new ItemLike[] {
                            WenyanItems.POWER_BLOCK_ITEM
                    },
                    new ItemLike[0]
            ),
            new Stage(
                    RunnerTier.RUNNER_6,
                    null,
                    new ItemLike[0],
                    new ItemLike[0],
                    new ItemLike[0]
            )
    };

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
        AdvancementUtils.unusedRegistry(registries);

        AdvancementHolder parent = AdvancementUtils.root(output,
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0),
                Component.literal("吾有一术"),
                Component.literal("获得初阶符"));

        for (Stage stage : STAGES) {
            addThrowRunner(output, stage.tier());
            if (stage.tier() == RunnerTier.RUNNER_0) {
                addThrowModule(output);
            }
            if (stage.nextTier() != null) {
                parent = addTier(output, parent, stage.nextTier());
            }
            addModules(output, stage.modules(), stage.parentPath());
            addTools(output, stage.toolBlocks(), stage.parentPath());
            addTools(output, stage.toolItems(), stage.parentPath());
        }
    }

    private AdvancementHolder addTier(Consumer<AdvancementHolder> output,
                                      AdvancementHolder parent,
                                      NextTier nextTier) {
        AdvancementHolder paperAdvancement = AdvancementUtils.item(output, "material/" + nextTier.paperPath(),
                parent, nextTier.paper());
        AdvancementHolder inkAdvancement = AdvancementUtils.item(output, "material/" + nextTier.inkPath(),
                paperAdvancement, nextTier.ink());
        return AdvancementUtils.item(output, runnerPath(nextTier.runnerTier()), inkAdvancement,
                WenyanItems.HAND_RUNNER.getItem(nextTier.runnerTier()));
    }

    private void addThrowRunner(Consumer<AdvancementHolder> output, RunnerTier tier) {
        AdvancementUtils.item(output, "throw/throw_runner_" + tier.ordinal(),
                AdvancementUtils.placeholder(parentPath(tier)), WenyanItems.THROW_RUNNER.getItem(tier));
    }

    private void addThrowModule(Consumer<AdvancementHolder> output) {
        AdvancementUtils.item(output, "throw/throw_module", AdvancementUtils.placeholder("root"),
                WenyanItems.THROW_MODULE.get());
    }

    private void addModules(Consumer<AdvancementHolder> output, ItemLike[] modules, String parentPath) {
        for (ItemLike module : modules) {
            AdvancementUtils.item(output, "module/" + path(module),
                    AdvancementUtils.placeholder(parentPath), module);
        }
    }

    private void addTools(Consumer<AdvancementHolder> output, ItemLike[] tools, String parentPath) {
        for (ItemLike tool : tools) {
            AdvancementUtils.item(output, "item/" + path(tool),
                    AdvancementUtils.placeholder(parentPath), tool);
        }
    }

    private static String parentPath(RunnerTier tier) {
        return tier == RunnerTier.RUNNER_0 ? "root" : runnerPath(tier);
    }

    private static String runnerPath(RunnerTier tier) {
        return "hand_runner_" + tier.ordinal();
    }

    private static String path(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private record Stage(RunnerTier tier,
                         NextTier nextTier,
                         ItemLike[] modules,
                         ItemLike[] toolBlocks,
                         ItemLike[] toolItems) {
        private String parentPath() {
            return RunnerAdvancements.parentPath(tier);
        }
    }

    private record NextTier(String paperPath,
                            ItemLike paper,
                            String inkPath,
                            ItemLike ink,
                            RunnerTier runnerTier) {
    }
}
