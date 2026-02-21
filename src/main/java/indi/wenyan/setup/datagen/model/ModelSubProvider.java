package indi.wenyan.setup.datagen.model;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public abstract class ModelSubProvider {
    protected final BlockModelGenerators blockModels;
    protected final ItemModelGenerators itemModels;

    protected ModelSubProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        this.blockModels = blockModels;
        this.itemModels = itemModels;
    }

    abstract protected void registerModels();
}
