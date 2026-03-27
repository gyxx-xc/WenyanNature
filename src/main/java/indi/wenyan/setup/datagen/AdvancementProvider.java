package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

@SuppressWarnings("ALL") // TODO: check needed
/**
 * Generates advancements for the Wenyan Programming mod.
 */
public final class AdvancementProvider {

    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        // Awarded when player get HAND_RUNNER
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(
                WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0),
                Component.literal("吾有一术"),
                Component.literal("第一个符咒！"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
        );
        builder.addCriterion("has_hand_runner",
                InventoryChangeTrigger.TriggerInstance.hasItems(WenyanItems.HAND_RUNNER.getItem(RunnerTier.RUNNER_0)));
        builder.save(saver, Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "woyouyishu"));
    }
}
