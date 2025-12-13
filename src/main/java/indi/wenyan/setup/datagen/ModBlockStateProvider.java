package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.BiConsumer;

/**
 * Provider for generating block states and models during data generation.
 * Defines the appearance of blocks in the game world.
 */
public class ModBlockStateProvider extends BlockStateProvider {

    /**
     * Constructs a new block state provider.
     * @param output The pack output for blockstate generation
     * @param modid The mod ID
     * @param exFileHelper Helper for accessing existing files
     */
    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.SCREEN_MODULE_BLOCK.get());
        simpleBlock(Registration.SEMAPHORE_MODULE_BLOCK.get());

        modeledBlock(this::horizontalFaceBlock, Registration.RUNNER_BLOCK);
        modeledBlock(this::simpleBlock, Registration.CRAFTING_BLOCK);
        modeledBlock(this::simpleBlock, Registration.PEDESTAL_BLOCK);

        registerModuleBlock(Registration.INFORMATION_MODULE_BLOCK);
        registerModuleBlock(Registration.BIT_MODULE_BLOCK);
        registerModuleBlock(Registration.BLOCK_MODULE_BLOCK);
    }

    private void modeledBlock(BiConsumer<Block, ModelFile> blockstateMethod, DeferredBlock<?> deferredBlock) {
        blockstateMethod.accept(deferredBlock.get(),
                new ModelFile.UncheckedModelFile(
                        ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
                                "block/" + deferredBlock.getKey().location().getPath())
                ));
    }

    /**
     * Registers a module block with standardized models.
     * @param deferredBlock The module block to register
     */
    private void registerModuleBlock(DeferredBlock<?> deferredBlock) {
        modeledBlock(this::horizontalFaceBlock, deferredBlock);
        String id = deferredBlock.getKey().location().getPath();
        models().singleTexture(id,
                ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block"),
                ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id));
    }
}
