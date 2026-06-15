package indi.wenyan.setup.datagen.advancement;

import indi.wenyan.WenyanProgramming;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public final class AdvancementUtils {
    private static final Component DEFAULT_DESCRIPTION = Component.literal("获得此物品");

    private AdvancementUtils() {
    }

    public static AdvancementHolder placeholder(String path) {
        return AdvancementSubProvider.createPlaceholder(WenyanProgramming.MODID + ":" + path);
    }

    public static AdvancementHolder item(Consumer<AdvancementHolder> output,
                                         String path,
                                         ItemLike item) {
        return item(output, path, null, item, Component.translatable(item.asItem().getDescriptionId()),
                DEFAULT_DESCRIPTION);
    }

    public static AdvancementHolder root(Consumer<AdvancementHolder> output,
                                         ItemLike item,
                                         Component title,
                                         Component description) {
        AdvancementHolder holder = Advancement.Builder.advancement()
                .display(item,
                        title,
                        description,
                        Identifier.fromNamespaceAndPath("minecraft", "gui/advancements/backgrounds/stone"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(item))
                .build(id("root"));
        output.accept(holder);
        return holder;
    }

    public static AdvancementHolder item(Consumer<AdvancementHolder> output,
                                         String path,
                                         AdvancementHolder parent,
                                         ItemLike item) {
        return item(output, path, parent, item, Component.translatable(item.asItem().getDescriptionId()),
                DEFAULT_DESCRIPTION);
    }

    public static AdvancementHolder item(Consumer<AdvancementHolder> output,
                                         String path,
                                         AdvancementHolder parent,
                                         ItemLike item,
                                         Component title,
                                         Component description) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .display(item, title, description, null, AdvancementType.TASK, true, true, false)
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(item));
        if (parent != null) {
            builder.parent(parent);
        }
        AdvancementHolder holder = builder.build(id(path));
        output.accept(holder);
        return holder;
    }

    public static void unusedRegistry(HolderLookup.Provider registries) {
        // Keeps AdvancementSubProvider implementations explicit about the vanilla signature.
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, path);
    }
}
