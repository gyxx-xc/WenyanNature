package indi.wenyan.setup.datagen.model;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Provider for generating block states and models during data generation.
 * Defines the appearance of blocks in the game world.
 */
public class ModBlockStateProvider extends ModelSubProvider {

    public ModBlockStateProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super(blockModels, itemModels);
    }

    @Override
    protected void registerModels() {
        blockModels.createTrivialCube(WenyanBlocks.SCREEN_MODULE_BLOCK.get());

        modeledBlock(blockModels::createHorizontallyRotatedBlock, WenyanBlocks.RUNNER_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.CRAFTING_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.PEDESTAL_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.POWER_BLOCK);

        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.LOCK_MODULE_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.FORMATION_CORE_MODULE_BLOCK);

//        blockModels.blockStateOutput.accept(
//                MultiVariantGenerator.dispatch(WenyanBlocks.LOCK_MODULE_BLOCK.get(),
//                        plainVariant()
//                        )
//        );
        registerModuleBlock(WenyanBlocks.EXPLOSION_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.INFORMATION_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.MATH_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.BIT_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.BLOCK_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.RANDOM_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.ITEM_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.VEC3_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.ENTITY_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.COMMUNICATE_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.COLLECTION_MODULE_BLOCK);
        registerModuleBlock(WenyanBlocks.STRING_MODULE_BLOCK);
    }

    private void modeledBlock(BiConsumer<Block, TexturedModel.Provider> blockstateMethod, DeferredBlock<?> deferredBlock) {
//        var templete = new ModelTemplate(Optional.of(
//                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
//                        "block/" + deferredBlock.getKey().identifier().getPath())),
//                Optional.empty());
//        blockstateMethod.accept(deferredBlock.get(),
//                block -> new TexturedModel(TextureMapping.defaultTexture(block), templete)
//        );
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(deferredBlock.get(),
                        BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                "block/" + deferredBlock.getKey().identifier().getPath())))
        );
    }

    /**
     * Registers a module block with standardized models.
     *
     * @param deferredBlock The module block to register
     */
    private void registerModuleBlock(DeferredBlock<?> deferredBlock) {
        var templete = new ModelTemplate(Optional.of(
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block")),
                Optional.empty(),
                TextureSlot.TEXTURE);
        blockModels.createHorizontallyRotatedBlock(deferredBlock.get(),
                block -> new TexturedModel(TextureMapping.defaultTexture(block), templete)
                );
    }
}
