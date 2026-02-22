package indi.wenyan.setup.datagen.model;

import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.BiConsumer;

/**
 * Provider for generating block states and models during data generation.
 * Defines the appearance of blocks in the game world.
 */
public class ModBlockStateProvider extends ModelSubProvider {

    protected ModBlockStateProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super(blockModels, itemModels);
    }

    @Override
    protected void registerModels() {
        blockModels.createTrivialCube(WenyanBlocks.SCREEN_MODULE_BLOCK.get());

        modeledBlock(blockModels::createHorizontallyRotatedBlock, WenyanBlocks.RUNNER_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.CRAFTING_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.PEDESTAL_BLOCK);
        modeledBlock(blockModels::createTrivialBlock, WenyanBlocks.POWER_BLOCK);
//        modeledBlock(this::simpleBlock, Registration.LOCK_MODULE_BLOCK);

//        getVariantBuilder(WenyanBlocks.LOCK_MODULE_BLOCK.get()).forAllStates(state -> {
//            boolean locked = state.getValue(LockModuleBlock.LOCK_STATE);
//
//            return ConfiguredModel.builder()
//                    .modelFile(locked ? new ModelFile.UncheckedModelFile(
//                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
//                                    "block/" + WenyanBlocks.LOCK_MODULE_BLOCK.getKey().location().getPath())
//                    ) : new ModelFile.UncheckedModelFile(
//                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
//                                    "block/" + WenyanBlocks.LOCK_MODULE_BLOCK.getKey().location().getPath() + "_1")
//                    ))
//                    .build();
//        });

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
//        blockstateMethod.accept(deferredBlock.get(),
//                new ModelFile.UncheckedModelFile(
//                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
//                                "block/" + deferredBlock.getKey().location().getPath())
//                ));
    }

    /**
     * Registers a module block with standardized models.
     *
     * @param deferredBlock The module block to register
     */
    private void registerModuleBlock(DeferredBlock<?> deferredBlock) {
//        modeledBlock(this::horizontalFaceBlock, deferredBlock);
//        String id = deferredBlock.getKey().location().getPath();
//        models().singleTexture(id,
//                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block"),
//                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id));
    }
}
