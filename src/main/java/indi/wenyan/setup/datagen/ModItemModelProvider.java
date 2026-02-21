package indi.wenyan.setup.datagen;

import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlock;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.content.block.power.PowerBlock;
import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Provider for generating item models during data generation.
 * Defines the appearance of items in inventory and when held.
 */
public class ModItemModelProvider extends ItemModelProvider {

    /**
     * Constructs a new item model provider.
     * @param output The pack output for model generation
     * @param modid The mod ID
     * @param existingFileHelper Helper for accessing existing files
     */
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(WenyanItems.HAND_RUNNER_0.get());
        basicItem(WenyanItems.HAND_RUNNER_1.get());
        basicItem(WenyanItems.HAND_RUNNER_2.get());
        basicItem(WenyanItems.HAND_RUNNER_3.get());

        basicItem(WenyanItems.BAMBOO_PAPER.get());
        basicItem(WenyanItems.CLOUD_PAPER.get());
        basicItem(WenyanItems.STAR_PAPER.get());
        basicItem(WenyanItems.FROST_PAPER.get());
        basicItem(WenyanItems.PHOENIX_PAPER.get());
        basicItem(WenyanItems.DRAGON_PAPER.get());


        basicItem(WenyanItems.ARCANE_INK.get());
        basicItem(WenyanItems.BAMBOO_INK.get());
        basicItem(WenyanItems.CELESTIAL_INK.get());
        basicItem(WenyanItems.LUNAR_INK.get());
        basicItem(WenyanItems.CINNABAR_INK.get());
        basicItem(WenyanItems.STARLIGHT_INK.get());

        basicItem(WenyanItems.FLOAT_NOTE.get());
        basicItem(WenyanItems.PRINT_INVENTORY_MODULE.get());

        basicItem(WenyanItems.BIT_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.ITEM_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.MATH_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.STRING_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.VEC3_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get());
        basicItem(WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get());

        basicItem(WenyanItems.EQUIPABLE_RUNNER_ITEM.get());

        blockItem(ScreenModuleBlock.ID);
        blockItem(LockModuleBlock.ID);
        blockItem(CraftingBlock.ID);
        blockItem(PedestalBlock.ID);
        blockItem(PowerBlock.ID);
    }

    private void blockItem(String item) {
        withExistingParent(item, modLoc("block/" + item));
    }
}
