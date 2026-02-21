package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.setup.Registration;
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
        simpleBlock(Registration.SCREEN_MODULE_BLOCK.get());

        modeledBlock(this::horizontalFaceBlock, Registration.RUNNER_BLOCK);
        modeledBlock(this::simpleBlock, Registration.CRAFTING_BLOCK);
        modeledBlock(this::simpleBlock, Registration.PEDESTAL_BLOCK);
        modeledBlock(this::simpleBlock, Registration.POWER_BLOCK);
//        modeledBlock(this::simpleBlock, Registration.LOCK_MODULE_BLOCK);

        getVariantBuilder(Registration.LOCK_MODULE_BLOCK.get()).forAllStates(state -> {
            boolean locked = state.getValue(LockModuleBlock.LOCK_STATE);

            return ConfiguredModel.builder()
                    .modelFile(locked ? new ModelFile.UncheckedModelFile(
                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                    "block/" + Registration.LOCK_MODULE_BLOCK.getKey().location().getPath())
                    ) : new ModelFile.UncheckedModelFile(
                            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                    "block/" + Registration.LOCK_MODULE_BLOCK.getKey().location().getPath() + "_1")
                    ))
                    .build();
        });

        registerModuleBlock(Registration.EXPLOSION_MODULE_BLOCK);
        registerModuleBlock(Registration.INFORMATION_MODULE_BLOCK);
        registerModuleBlock(Registration.MATH_MODULE_BLOCK);
        registerModuleBlock(Registration.BIT_MODULE_BLOCK);
        registerModuleBlock(Registration.BLOCK_MODULE_BLOCK);
        registerModuleBlock(Registration.RANDOM_MODULE_BLOCK);
        registerModuleBlock(Registration.ITEM_MODULE_BLOCK);
        registerModuleBlock(Registration.VEC3_MODULE_BLOCK);
        registerModuleBlock(Registration.ENTITY_MODULE_BLOCK);
        registerModuleBlock(Registration.COMMUNICATE_MODULE_BLOCK);
        registerModuleBlock(Registration.COLLECTION_MODULE_BLOCK);
        registerModuleBlock(Registration.STRING_MODULE_BLOCK);
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
