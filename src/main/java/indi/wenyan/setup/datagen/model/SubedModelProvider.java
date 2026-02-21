package indi.wenyan.setup.datagen.model;

import indi.wenyan.WenyanProgramming;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SubedModelProvider extends ModelProvider {
    private final ModelSubProviderSupplier supplier;

    public SubedModelProvider(PackOutput output, ModelSubProviderSupplier supplier) {
        super(output, WenyanProgramming.MODID);
        this.supplier = supplier;
    }

    public static DataProvider.Factory of(ModelSubProviderSupplier supplier) {
        return output -> new SubedModelProvider(output, supplier);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        supplier.create(blockModels, itemModels).registerModels();
    }

    @FunctionalInterface
    public interface ModelSubProviderSupplier {
        ModelSubProvider create(BlockModelGenerators blockModels, ItemModelGenerators itemModels);
    }
}
