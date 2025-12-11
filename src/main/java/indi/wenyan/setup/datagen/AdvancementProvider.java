package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

@SuppressWarnings("ALL") // TODO: check needed
/**
 * Generates advancements for the Wenyan Programming mod.
 */
public final class AdvancementProvider {

    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        // Awarded when player get HAND_RUNNER
        Advancement.Builder builder = Advancement.Builder.advancement();
        builder.display(
                new ItemStack(Registration.HAND_RUNNER_0.get()),
                Component.literal("吾有一术"),
                Component.literal("第一个符咒！"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
        );
        builder.addCriterion("has_hand_runner",
                InventoryChangeTrigger.TriggerInstance.hasItems(Registration.HAND_RUNNER_0.get()));
        builder.save(saver, ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "woyouyishu"), existingFileHelper);
    }
}
