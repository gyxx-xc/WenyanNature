package indi.wenyan.setup.definitions;

import indi.wenyan.WenyanProgramming;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public enum WenyanItems {
    ;
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WenyanProgramming.MODID);

    // Hand Runner items
    public static final DeferredItem<Item> HAND_RUNNER_0;
    public static final DeferredItem<Item> HAND_RUNNER_1;
    public static final DeferredItem<Item> HAND_RUNNER_2;
    public static final DeferredItem<Item> HAND_RUNNER_3;
    public static final DeferredItem<Item> EQUIPABLE_RUNNER_ITEM;
    public static final DeferredItem<Item> PRINT_INVENTORY_MODULE;
    public static final DeferredItem<Item> FLOAT_NOTE;
    // Paper items
    public static final DeferredItem<Item> BAMBOO_PAPER;
    public static final DeferredItem<Item> CLOUD_PAPER;
    public static final DeferredItem<Item> FROST_PAPER;
    public static final DeferredItem<Item> PHOENIX_PAPER;
    public static final DeferredItem<Item> STAR_PAPER;
    public static final DeferredItem<Item> DRAGON_PAPER;
    // Ink items
    public static final DeferredItem<Item> ARCANE_INK;
    public static final DeferredItem<Item> BAMBOO_INK;
    public static final DeferredItem<Item> CELESTIAL_INK;
    public static final DeferredItem<Item> CINNABAR_INK;
    public static final DeferredItem<Item> LUNAR_INK;
    public static final DeferredItem<Item> STARLIGHT_INK;
    public static final DeferredItem<BlockItem> CRAFTING_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> PEDESTAL_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> POWER_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> EXPLOSION_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> INFORMATION_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> MATH_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> BIT_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> BLOCK_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> RANDOM_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> ITEM_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> VEC3_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> COMMUNICATE_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> COLLECTION_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> STRING_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> ENTITY_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> SCREEN_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> LOCK_MODULE_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> FORMATION_CORE_MODULE_BLOCK_ITEM;

    static {
        WenyanItems.HAND_RUNNER_0 = ITEMS.registerItem(RunnerItem.ID_0,
                (Item.Properties properties) -> new RunnerItem(properties, 0));
        WenyanItems.HAND_RUNNER_1 = ITEMS.registerItem(RunnerItem.ID_1,
                (Item.Properties properties) -> new RunnerItem(properties, 1));
        WenyanItems.HAND_RUNNER_2 = ITEMS.registerItem(RunnerItem.ID_2,
                (Item.Properties properties) -> new RunnerItem(properties, 2));
        WenyanItems.HAND_RUNNER_3 = ITEMS.registerItem(RunnerItem.ID_3,
                (Item.Properties properties) -> new RunnerItem(properties, 3));

        WenyanItems.EQUIPABLE_RUNNER_ITEM = ITEMS.registerItem(EquipableRunnerItem.ID_1,
                (Item.Properties properties) -> new EquipableRunnerItem(properties, 1));
        WenyanItems.PRINT_INVENTORY_MODULE = ITEMS.registerItem(PrintInventoryModule.ID, PrintInventoryModule::new);

        WenyanItems.FLOAT_NOTE = ITEMS.registerItem(FloatNoteItem.ID, FloatNoteItem::new);

        // Paper
        WenyanItems.BAMBOO_PAPER = ITEMS.registerItem(BambooPaper.ID, BambooPaper::new);
        WenyanItems.CLOUD_PAPER = ITEMS.registerItem(CloudPaper.ID, CloudPaper::new);
        WenyanItems.STAR_PAPER = ITEMS.registerItem(StarPaper.ID, StarPaper::new);
        WenyanItems.FROST_PAPER = ITEMS.registerItem(FrostPaper.ID, FrostPaper::new);
        WenyanItems.PHOENIX_PAPER = ITEMS.registerItem(PhoenixPaper.ID, PhoenixPaper::new);
        WenyanItems.DRAGON_PAPER = ITEMS.registerItem(DragonPaper.ID, DragonPaper::new);

        // Ink
        WenyanItems.ARCANE_INK = ITEMS.registerItem(ArcaneInk.ID, ArcaneInk::new);
        WenyanItems.BAMBOO_INK = ITEMS.registerItem(BambooInk.ID, BambooInk::new);
        WenyanItems.CELESTIAL_INK = ITEMS.registerItem(CelestialInk.ID, CelestialInk::new);
        WenyanItems.CINNABAR_INK = ITEMS.registerItem(CinnabarInk.ID, CinnabarInk::new);
        WenyanItems.LUNAR_INK = ITEMS.registerItem(LunarInk.ID, LunarInk::new);
        WenyanItems.STARLIGHT_INK = ITEMS.registerItem(StarlightInk.ID, StarlightInk::new);

        WenyanItems.CRAFTING_BLOCK_ITEM = ITEMS.registerItem(CraftingBlock.ID,
                properties -> new BlockItem(WenyanBlocks.CRAFTING_BLOCK.get(), properties));

        WenyanItems.PEDESTAL_BLOCK_ITEM = ITEMS.registerItem(PedestalBlock.ID,
                properties -> new BlockItem(WenyanBlocks.PEDESTAL_BLOCK.get(), properties));
        WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM = ITEMS.registerItem(ExplosionModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.EXPLOSION_MODULE_BLOCK.get(), properties));
        WenyanItems.INFORMATION_MODULE_BLOCK_ITEM = ITEMS.registerItem(WorldModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.INFORMATION_MODULE_BLOCK.get(), properties));
        WenyanItems.MATH_MODULE_BLOCK_ITEM = ITEMS.registerItem(MathModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.MATH_MODULE_BLOCK.get(), properties));
        WenyanItems.BIT_MODULE_BLOCK_ITEM = ITEMS.registerItem(BitModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.BIT_MODULE_BLOCK.get(), properties));
        WenyanItems.BLOCK_MODULE_BLOCK_ITEM = ITEMS.registerItem(BlockModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.BLOCK_MODULE_BLOCK.get(), properties));
        WenyanItems.RANDOM_MODULE_BLOCK_ITEM = ITEMS.registerItem(RandomModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.RANDOM_MODULE_BLOCK.get(), properties));
        WenyanItems.ITEM_MODULE_BLOCK_ITEM = ITEMS.registerItem(ItemModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.ITEM_MODULE_BLOCK.get(), properties));
        WenyanItems.VEC3_MODULE_BLOCK_ITEM = ITEMS.registerItem(Vec3ModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.VEC3_MODULE_BLOCK.get(), properties));
        WenyanItems.SCREEN_MODULE_BLOCK_ITEM = ITEMS.registerItem(ScreenModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.SCREEN_MODULE_BLOCK.get(), properties));
        WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM = ITEMS.registerItem(CommunicateModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.COMMUNICATE_MODULE_BLOCK.get(), properties));
        WenyanItems.LOCK_MODULE_BLOCK_ITEM = ITEMS.registerItem(LockModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.LOCK_MODULE_BLOCK.get(), properties));
        WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM = ITEMS.registerItem(FormationCoreModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.FORMATION_CORE_MODULE_BLOCK.get(), properties));
        WenyanItems.COLLECTION_MODULE_BLOCK_ITEM = ITEMS.registerItem(CollectionModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.COLLECTION_MODULE_BLOCK.get(), properties));
        WenyanItems.STRING_MODULE_BLOCK_ITEM = ITEMS.registerItem(StringModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.STRING_MODULE_BLOCK.get(), properties));
        WenyanItems.ENTITY_MODULE_BLOCK_ITEM = ITEMS.registerItem(EntityModuleBlock.ID,
                properties -> new BlockItem(WenyanBlocks.ENTITY_MODULE_BLOCK.get(), properties));
        WenyanItems.POWER_BLOCK_ITEM = ITEMS.registerItem(PowerBlock.ID,
                properties -> new BlockItem(WenyanBlocks.POWER_BLOCK.get(), properties));
    }
}
