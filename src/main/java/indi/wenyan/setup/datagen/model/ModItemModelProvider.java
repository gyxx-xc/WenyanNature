package indi.wenyan.setup.datagen.model;

import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Provider for generating item models during data generation.
 * Defines the appearance of items in inventory and when held.
 */
@ParametersAreNonnullByDefault
public class ModItemModelProvider extends ModelSubProvider {
    public ModItemModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super(blockModels, itemModels);
    }

    @Override
    protected void registerModels() {
        basicItem(WenyanItems.HAND_RUNNER_0.get());
        basicItem(WenyanItems.HAND_RUNNER_1.get());
        basicItem(WenyanItems.HAND_RUNNER_2.get());
        basicItem(WenyanItems.HAND_RUNNER_3.get());
        basicItem(WenyanItems.HAND_RUNNER_4.get());
        basicItem(WenyanItems.HAND_RUNNER_5.get());

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
    }

    private void basicItem(Item item) {
        var model = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(item),
                itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }
}
