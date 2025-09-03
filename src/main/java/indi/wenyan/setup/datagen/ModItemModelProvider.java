package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.Registration;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Provider for generating item models during data generation.
 * Defines the appearance of items in inventory and when held.
 */
@ParametersAreNonnullByDefault
public class ModItemModelProvider extends ModelProvider {

    /**
     * Constructs a new item model provider.
     * @param output The pack output for model generation
     */
    public ModItemModelProvider(PackOutput output) {
        super(output, WenyanProgramming.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(Registration.HAND_RUNNER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HAND_RUNNER_1.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HAND_RUNNER_2.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HAND_RUNNER_3.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(Registration.BAMBOO_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CLOUD_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.DRAGON_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.FROST_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.PHOENIX_PAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.STAR_PAPER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(Registration.ARCANE_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.BAMBOO_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CELESTIAL_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.LUNAR_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CINNABAR_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.STARLIGHT_INK.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(Registration.FLOAT_NOTE.get(), ModelTemplates.FLAT_ITEM);
    }
}
