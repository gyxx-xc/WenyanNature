package indi.wenyan.setup.datagen;

import indi.wenyan.content.block.additional_module.block.ScreenModuleBlock;
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
        basicItem(Registration.HAND_RUNNER.get());
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

        blockItem(ScreenModuleBlock.ID);
    }

    private void blockItem(String item) {
        withExistingParent(item, modLoc("block/" + item));
    }
}
