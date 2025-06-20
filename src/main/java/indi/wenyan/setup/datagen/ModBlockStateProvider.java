package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanNature;
import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.horizontalFaceBlock(Registration.RUNNER_BLOCK.get(),
                new ModelFile.UncheckedModelFile(
                        ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "block/runner_block")));

    }
}
