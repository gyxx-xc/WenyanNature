package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.Registration;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 * Provider for generating block states and models during data generation.
 * Defines the appearance of blocks in the game world.
 */
@ParametersAreNonnullByDefault
public class ModBlockStateProvider extends ModelProvider {
    public static final ModelTemplate RUNNER_TEMPLATE =
            new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
                    "block/template_runner_block")), Optional.empty(), TextureSlot.TEXTURE);

    /**
     * Constructs a new block state provider.
     * @param output The pack output for blockstate generation
     */
    public ModBlockStateProvider(PackOutput output) {
        super(output, WenyanProgramming.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.familyWithExistingFullBlock(Registration.RUNNER_BLOCK.get())
                .fullBlock(Registration.RUNNER_BLOCK.get(), RUNNER_TEMPLATE);

//        simpleBlock(Registration.CRAFTING_BLOCK.get(),
//                new ModelFile.UncheckedModelFile(
//                        ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/crafting_block")));
        registerModuleBlock(blockModels, Registration.INFORMATION_MODULE_BLOCK);
        registerModuleBlock(blockModels, Registration.INTERACT_MODULE_BLOCK);
        registerModuleBlock(blockModels, Registration.BIT_MODULE_BLOCK);
        registerModuleBlock(blockModels, Registration.INVENTORY_MODULE_BLOCK);
        registerModuleBlock(blockModels, Registration.BLOCK_MODULE_BLOCK);
    }

    /**
     * Registers a module block with standardized models.
     * @param deferredBlock The module block to register
     */
    private void registerModuleBlock(BlockModelGenerators blockModels,
                                     DeferredBlock<?> deferredBlock) {
        var block = deferredBlock.get();
//        horizontalFaceBlock(block,
//                new ModelFile.UncheckedModelFile(
//                        ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id)));
//        models().singleTexture(id,
//                ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block"),
//                ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id));
//
//        blockModels.createAxisAlignedPillarBlockCustomModel(deferredBlock.get(),
//                BlockModelGenerators.plainVariant(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/runner_block")));
        blockModels.familyWithExistingFullBlock(block)
                .fullBlock(block, RUNNER_TEMPLATE);
//        blockModels.modelOutput(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
//                "block/" + id), (ModelInstance) () -> {
//            return models().getExistingFile(
//                    ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id));
//        });
    }
}
