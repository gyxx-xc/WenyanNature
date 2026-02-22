package indi.wenyan.setup.datagen.model;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.setup.definitions.WYRegistration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
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
     *
     * @param output       The pack output for blockstate generation
     * @param modid        The mod ID
     * @param exFileHelper Helper for accessing existing files
     */
    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(WYRegistration.SCREEN_MODULE_BLOCK.get());

        modeledBlock(this::horizontalFaceBlock, WYRegistration.RUNNER_BLOCK);
        modeledBlock(this::simpleBlock, WYRegistration.CRAFTING_BLOCK);
        modeledBlock(this::simpleBlock, WYRegistration.PEDESTAL_BLOCK);
        modeledBlock(this::simpleBlock, WYRegistration.POWER_BLOCK);
//        modeledBlock(this::simpleBlock, Registration.LOCK_MODULE_BLOCK);

        getVariantBuilder(WYRegistration.LOCK_MODULE_BLOCK.get()).forAllStates(state -> {
            boolean locked = state.getValue(LockModuleBlock.LOCK_STATE);

            return ConfiguredModel.builder()
                    .modelFile(locked ? new ModelFile.UncheckedModelFile(
                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                    "block/" + WYRegistration.LOCK_MODULE_BLOCK.getKey().location().getPath())
                    ) : new ModelFile.UncheckedModelFile(
                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                    "block/" + WYRegistration.LOCK_MODULE_BLOCK.getKey().location().getPath() + "_1")
                    ))
                    .build();
        });

        registerModuleBlock(WYRegistration.EXPLOSION_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.INFORMATION_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.MATH_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.BIT_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.BLOCK_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.RANDOM_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.ITEM_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.VEC3_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.ENTITY_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.COMMUNICATE_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.COLLECTION_MODULE_BLOCK);
        registerModuleBlock(WYRegistration.STRING_MODULE_BLOCK);
    }

    private void modeledBlock(BiConsumer<Block, ModelFile> blockstateMethod, DeferredBlock<?> deferredBlock) {
        blockstateMethod.accept(deferredBlock.get(),
                new ModelFile.UncheckedModelFile(
                        Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                "block/" + deferredBlock.getKey().location().getPath())
                ));
    }

    /**
     * Registers a module block with standardized models.
     *
     * @param deferredBlock The module block to register
     */
    private void registerModuleBlock(DeferredBlock<?> deferredBlock) {
        modeledBlock(this::horizontalFaceBlock, deferredBlock);
        String id = deferredBlock.getKey().location().getPath();
        models().singleTexture(id,
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block"),
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/" + id));
    }
}
