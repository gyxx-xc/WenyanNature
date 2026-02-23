package indi.wenyan.setup.datagen.model;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Optional;

import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

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

        modeledBlock(WenyanBlocks.CRAFTING_BLOCK);
        modeledBlock(WenyanBlocks.PEDESTAL_BLOCK);
        modeledBlock(WenyanBlocks.POWER_BLOCK);
        modeledBlock(WenyanBlocks.FORMATION_CORE_MODULE_BLOCK);

//        modeledBlock(WenyanBlocks.LOCK_MODULE_BLOCK);
        MultiVariant off = plainVariant(ModelLocationUtils.getModelLocation(WenyanBlocks.LOCK_MODULE_BLOCK.get()));
        MultiVariant on = plainVariant(ModelLocationUtils.getModelLocation(WenyanBlocks.LOCK_MODULE_BLOCK.get(), "_1"));
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                        .dispatch(WenyanBlocks.LOCK_MODULE_BLOCK.get())
                        .with(createBooleanModelDispatch(LockModuleBlock.LOCK_STATE, off, on)));

        registerFuluBlock(WenyanBlocks.RUNNER_BLOCK);
        registerFuluBlock(WenyanBlocks.EXPLOSION_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.INFORMATION_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.MATH_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.BIT_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.BLOCK_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.RANDOM_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.ITEM_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.VEC3_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.ENTITY_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.COMMUNICATE_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.COLLECTION_MODULE_BLOCK);
        registerFuluBlock(WenyanBlocks.STRING_MODULE_BLOCK);
    }

    private void modeledBlock(DeferredBlock<?> deferredBlock) {
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(deferredBlock.get(),
                        plainVariant(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
                                "block/" + deferredBlock.getKey().identifier().getPath())))
        );
    }

    /**
     * Registers a module block with standardized models.
     *
     * @param deferredBlock The module block to register
     */
    private void registerFuluBlock(DeferredBlock<?> deferredBlock) {
        var templete = new ModelTemplate(Optional.of(
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block")),
                Optional.empty(),
                TextureSlot.TEXTURE);
        blockModels.createHorizontallyRotatedBlock(deferredBlock.get(), block ->
                new TexturedModel(TextureMapping.defaultTexture(block), templete));
    }
}
