package indi.wenyan.setup.datagen;

import indi.wenyan.content.block.additional_module.block.ScreenModuleBlock;
import indi.wenyan.content.block.additional_module.block.SemaphoreModuleBlock;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.setup.Registration;
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
        basicItem(Registration.HAND_RUNNER_0.get());
        basicItem(Registration.HAND_RUNNER_1.get());
        basicItem(Registration.HAND_RUNNER_2.get());
        basicItem(Registration.HAND_RUNNER_3.get());

        basicItem(Registration.BAMBOO_PAPER.get());
        basicItem(Registration.CLOUD_PAPER.get());
        basicItem(Registration.DRAGON_PAPER.get());
        basicItem(Registration.FROST_PAPER.get());
        basicItem(Registration.PHOENIX_PAPER.get());
        basicItem(Registration.STAR_PAPER.get());

        basicItem(Registration.ARCANE_INK.get());
        basicItem(Registration.BAMBOO_INK.get());
        basicItem(Registration.CELESTIAL_INK.get());
        basicItem(Registration.LUNAR_INK.get());
        basicItem(Registration.CINNABAR_INK.get());
        basicItem(Registration.STARLIGHT_INK.get());

        basicItem(Registration.FLOAT_NOTE.get());
        basicItem(Registration.PRINT_INVENTORY_MODULE.get());

        basicItem(Registration.BIT_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.BLOCK_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.ITEM_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.COMMUNICATE_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.ENTITY_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.EXPLOSION_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.INFORMATION_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.MATH_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.STRING_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.VEC3_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.RANDOM_MODULE_BLOCK_ITEM.get());
        basicItem(Registration.COLLECTION_MODULE_BLOCK_ITEM.get());

        blockItem(ScreenModuleBlock.ID);
        blockItem(SemaphoreModuleBlock.ID);
        blockItem(CraftingBlock.ID);
        blockItem(PedestalBlock.ID);
    }

    private void blockItem(String item) {
        withExistingParent(item, modLoc("block/" + item));
    }
}
