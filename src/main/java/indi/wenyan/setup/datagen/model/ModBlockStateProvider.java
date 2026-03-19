package indi.wenyan.setup.datagen.model;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Optional;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

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

        writingBlock();
        lockModuleBlock();
        decorativePistonHeads();

        for (var block : WenyanBlocks.RUNNER_BLOCK.getBlocks()){
            registerFuluBlock(block);
        }

        registerFuluBlock(WenyanBlocks.EXPLOSION_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.INFORMATION_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.MATH_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.BIT_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.BLOCK_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.RANDOM_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.ITEM_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.VEC3_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.ENTITY_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.COMMUNICATE_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.COLLECTION_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.STRING_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.BLOCKING_QUEUE_MODULE_BLOCK.get());
        registerFuluBlock(WenyanBlocks.PISTON_MODULE_BLOCK.get());
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
     * @param block The module block to register
     */
    private void registerFuluBlock(Block block) {
        var templete = new ModelTemplate(Optional.of(
                Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "block/template_runner_block")),
                Optional.empty(),
                TextureSlot.TEXTURE);
        MultiVariant model = plainVariant((new TexturedModel(TextureMapping.defaultTexture(block), templete)).create(block, blockModels.modelOutput));
        // copy from lever
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180)).select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90)).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.WALL, Direction.NORTH, X_ROT_90).select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90)).select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270))));
    }

    public void writingBlock() {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(WenyanBlocks.WRITING_BLOCK.get(), "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(WenyanBlocks.WRITING_BLOCK.get(), "_side"));
        blockModels.blockStateOutput
                .accept(createSimpleBlock(WenyanBlocks.WRITING_BLOCK.get(), plainVariant(ModelTemplates.CUBE_TOP.create(WenyanBlocks.WRITING_BLOCK.get(), mapping, blockModels.modelOutput))));
    }

    private void lockModuleBlock() {
        MultiVariant off = plainVariant(ModelLocationUtils.getModelLocation(WenyanBlocks.LOCK_MODULE_BLOCK.get()));
        MultiVariant on = plainVariant(ModelLocationUtils.getModelLocation(WenyanBlocks.LOCK_MODULE_BLOCK.get(), "_1"));
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(WenyanBlocks.LOCK_MODULE_BLOCK.get())
                .with(createBooleanModelDispatch(LockModuleBlock.LOCK_STATE, off, on)));
    }

    public void decorativePistonHeads() {
        TextureMapping commonMapping = (new TextureMapping())
                .put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        TextureMapping stickyTextures = commonMapping.copyAndUpdate(TextureSlot.PLATFORM,
                TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
        TextureMapping normalTextures = commonMapping.copyAndUpdate(TextureSlot.PLATFORM,
                TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(WenyanBlocks.DECORATIVE_PISTON_HEAD_BLOCK.get())
                .with(PropertyDispatch
                        .initial(BlockStateProperties.PISTON_TYPE)
                        .select(PistonType.DEFAULT, plainVariant(ModelTemplates.PISTON_HEAD
                                .createWithSuffix(Blocks.PISTON, "_head", normalTextures, blockModels.modelOutput)))
                        .select(PistonType.STICKY, plainVariant(ModelTemplates.PISTON_HEAD
                                .createWithSuffix(Blocks.PISTON, "_head_sticky", stickyTextures, blockModels.modelOutput))))
                .with(ROTATION_FACING));
    }
}
