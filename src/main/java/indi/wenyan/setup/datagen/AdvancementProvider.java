package indi.wenyan.setup.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.function.Consumer;

@SuppressWarnings("ALL") // TODO: check needed
/**
 * Generates advancements for the Wenyan Programming mod.
 */
public final class AdvancementProvider {

    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        // Awarded when player get HAND_RUNNER
//        Advancement.Builder builder = Advancement.Builder.advancement();
//        builder.display(
//                new ItemStack(WenyanItems.HAND_RUNNER_0.get()),
//                Component.literal("吾有一术"),
//                Component.literal("第一个符咒！"),
//                null,
//                AdvancementType.TASK,
//                true,
//                true,
//                false
//        );
//        builder.addCriterion("has_hand_runner",
//                InventoryChangeTrigger.TriggerInstance.hasItems(WenyanItems.HAND_RUNNER_0.get()));
//        builder.save(saver, Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "woyouyishu"), existingFileHelper);
    }
}
