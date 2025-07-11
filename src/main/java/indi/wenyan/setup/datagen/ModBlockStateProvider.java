package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
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
        horizontalFaceBlock(Registration.RUNNER_BLOCK.get(),
                new ModelFile.UncheckedModelFile(
                        ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/runner_block")));
        horizontalFaceBlock(Registration.ADDITIONAL_PAPER_BLOCK.get(),
                new ModelFile.UncheckedModelFile(
                        ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/runner_block")));
    }
}
