package indi.wenyan.setup.definitions;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.FormationCoreModuleBlock;
import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlock;
import indi.wenyan.content.block.additional_module.builtin.*;
import indi.wenyan.content.block.additional_module.paper.*;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.content.block.power.PowerBlock;
import indi.wenyan.content.item.EquipableRunnerItem;
import indi.wenyan.content.item.FloatNoteItem;
import indi.wenyan.content.item.RunnerItem;
import indi.wenyan.content.item.additional_module.PrintInventoryModule;
import indi.wenyan.content.item.ink.*;
import indi.wenyan.content.item.paper.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public enum WenyanItems {
    ;
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(WenyanProgramming.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WenyanProgramming.MODID);

    // Hand Runner items
    public static final DeferredItem<Item> HAND_RUNNER_0 = DR.registerItem(RunnerItem.ID_0,
            (Item.Properties properties) -> new RunnerItem(properties, 0));
    public static final DeferredItem<Item> HAND_RUNNER_1 = DR.registerItem(RunnerItem.ID_1,
            (Item.Properties properties) -> new RunnerItem(properties, 1));
    public static final DeferredItem<Item> HAND_RUNNER_2 = DR.registerItem(RunnerItem.ID_2,
            (Item.Properties properties) -> new RunnerItem(properties, 2));
    public static final DeferredItem<Item> HAND_RUNNER_3 = DR.registerItem(RunnerItem.ID_3,
            (Item.Properties properties) -> new RunnerItem(properties, 3));
    public static final DeferredItem<Item> EQUIPABLE_RUNNER_ITEM = DR.registerItem(EquipableRunnerItem.ID_1,
            (Item.Properties properties) -> new EquipableRunnerItem(properties, 1));
    public static final DeferredItem<Item> PRINT_INVENTORY_MODULE = DR.registerItem(PrintInventoryModule.ID, PrintInventoryModule::new);
    public static final DeferredItem<Item> FLOAT_NOTE = DR.registerItem(FloatNoteItem.ID, FloatNoteItem::new);
    // Paper items
    public static final DeferredItem<Item> BAMBOO_PAPER = DR.registerItem(BambooPaper.ID, BambooPaper::new);
    public static final DeferredItem<Item> CLOUD_PAPER = DR.registerItem(CloudPaper.ID, CloudPaper::new);
    public static final DeferredItem<Item> FROST_PAPER = DR.registerItem(FrostPaper.ID, FrostPaper::new);
    public static final DeferredItem<Item> PHOENIX_PAPER = DR.registerItem(PhoenixPaper.ID, PhoenixPaper::new);
    public static final DeferredItem<Item> STAR_PAPER = DR.registerItem(StarPaper.ID, StarPaper::new);
    public static final DeferredItem<Item> DRAGON_PAPER = DR.registerItem(DragonPaper.ID, DragonPaper::new);
    // Ink items
    public static final DeferredItem<Item> ARCANE_INK = DR.registerItem(ArcaneInk.ID, ArcaneInk::new);
    public static final DeferredItem<Item> BAMBOO_INK = DR.registerItem(BambooInk.ID, BambooInk::new);
    public static final DeferredItem<Item> CELESTIAL_INK = DR.registerItem(CelestialInk.ID, CelestialInk::new);
    public static final DeferredItem<Item> CINNABAR_INK = DR.registerItem(CinnabarInk.ID, CinnabarInk::new);
    public static final DeferredItem<Item> LUNAR_INK = DR.registerItem(LunarInk.ID, LunarInk::new);
    public static final DeferredItem<Item> STARLIGHT_INK = DR.registerItem(StarlightInk.ID, StarlightInk::new);
    public static final DeferredItem<BlockItem> CRAFTING_BLOCK_ITEM = DR.registerItem(CraftingBlock.ID,
            properties -> new BlockItem(WenyanBlocks.CRAFTING_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> PEDESTAL_BLOCK_ITEM = DR.registerItem(PedestalBlock.ID,
            properties -> new BlockItem(WenyanBlocks.PEDESTAL_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> POWER_BLOCK_ITEM = DR.registerItem(PowerBlock.ID,
            properties -> new BlockItem(WenyanBlocks.POWER_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> EXPLOSION_MODULE_BLOCK_ITEM = DR.registerItem(ExplosionModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.EXPLOSION_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> INFORMATION_MODULE_BLOCK_ITEM = DR.registerItem(WorldModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.INFORMATION_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> MATH_MODULE_BLOCK_ITEM = DR.registerItem(MathModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.MATH_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> BIT_MODULE_BLOCK_ITEM = DR.registerItem(BitModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.BIT_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> BLOCK_MODULE_BLOCK_ITEM = DR.registerItem(BlockModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.BLOCK_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> RANDOM_MODULE_BLOCK_ITEM = DR.registerItem(RandomModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.RANDOM_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> ITEM_MODULE_BLOCK_ITEM = DR.registerItem(ItemModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.ITEM_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> VEC3_MODULE_BLOCK_ITEM = DR.registerItem(Vec3ModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.VEC3_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> COMMUNICATE_MODULE_BLOCK_ITEM = DR.registerItem(CommunicateModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.COMMUNICATE_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> COLLECTION_MODULE_BLOCK_ITEM = DR.registerItem(CollectionModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.COLLECTION_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> STRING_MODULE_BLOCK_ITEM = DR.registerItem(StringModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.STRING_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> ENTITY_MODULE_BLOCK_ITEM = DR.registerItem(EntityModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.ENTITY_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> SCREEN_MODULE_BLOCK_ITEM = DR.registerItem(ScreenModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.SCREEN_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> LOCK_MODULE_BLOCK_ITEM = DR.registerItem(LockModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.LOCK_MODULE_BLOCK.get(), properties));
    public static final DeferredItem<BlockItem> FORMATION_CORE_MODULE_BLOCK_ITEM = DR.registerItem(FormationCoreModuleBlock.ID,
            properties -> new BlockItem(WenyanBlocks.FORMATION_CORE_MODULE_BLOCK.get(), properties));

    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("wenyan_programming", () -> CreativeModeTab.builder()
            .title(Component.translatable("title.wenyan_programming.create_tab"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> HAND_RUNNER_1.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(HAND_RUNNER_0.get());
                output.accept(HAND_RUNNER_1.get());
                output.accept(HAND_RUNNER_2.get());
                output.accept(HAND_RUNNER_3.get());

                output.accept(BAMBOO_PAPER.get());
                output.accept(CLOUD_PAPER.get());
                output.accept(STAR_PAPER.get());
                output.accept(FROST_PAPER.get());
                output.accept(PHOENIX_PAPER.get());
                output.accept(DRAGON_PAPER.get());

                output.accept(BAMBOO_INK.get());
                output.accept(CINNABAR_INK.get());
                output.accept(STARLIGHT_INK.get());
                output.accept(LUNAR_INK.get());
                output.accept(CELESTIAL_INK.get());
                output.accept(ARCANE_INK.get());

                output.accept(EQUIPABLE_RUNNER_ITEM.get());
                output.accept(PRINT_INVENTORY_MODULE.get());

                output.accept(FLOAT_NOTE.get());
                output.accept(CRAFTING_BLOCK_ITEM.get());
                output.accept(PEDESTAL_BLOCK_ITEM.get());

                output.accept(BIT_MODULE_BLOCK_ITEM.get());
                output.accept(MATH_MODULE_BLOCK_ITEM.get());
                output.accept(VEC3_MODULE_BLOCK_ITEM.get());
                output.accept(RANDOM_MODULE_BLOCK_ITEM.get());
                output.accept(STRING_MODULE_BLOCK_ITEM.get());
                output.accept(COLLECTION_MODULE_BLOCK_ITEM.get());

                output.accept(ITEM_MODULE_BLOCK_ITEM.get());
                output.accept(BLOCK_MODULE_BLOCK_ITEM.get());
                output.accept(ENTITY_MODULE_BLOCK_ITEM.get());
                output.accept(INFORMATION_MODULE_BLOCK_ITEM.get());

                output.accept(EXPLOSION_MODULE_BLOCK_ITEM.get());

                output.accept(FORMATION_CORE_MODULE_BLOCK_ITEM.get());
                output.accept(COMMUNICATE_MODULE_BLOCK_ITEM.get());
                output.accept(LOCK_MODULE_BLOCK_ITEM.get());
                output.accept(SCREEN_MODULE_BLOCK_ITEM.get());

                output.accept(POWER_BLOCK_ITEM.get());
            }).build());
}

