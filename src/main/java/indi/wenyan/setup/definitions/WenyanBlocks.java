package indi.wenyan.setup.definitions;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.*;
import indi.wenyan.content.block.additional_module.builtin.*;
import indi.wenyan.content.block.additional_module.paper.*;
import indi.wenyan.content.block.crafting_block.CraftingBlock;
import indi.wenyan.content.block.crafting_block.CraftingBlockEntity;
import indi.wenyan.content.block.pedestal.PedestalBlock;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import indi.wenyan.content.block.power.PowerBlock;
import indi.wenyan.content.block.power.PowerBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public enum WenyanBlocks {
    ;
    public static final DeferredRegister<BlockEntityType<?>> DR_ENTITY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, WenyanProgramming.MODID);
    public static final DeferredRegister.Blocks DR =
            DeferredRegister.createBlocks(WenyanProgramming.MODID);

    public static final DeferredBlock<RunnerBlock> RUNNER_BLOCK = WenyanBlocks.DR.registerBlock(RunnerBlock.ID, RunnerBlock::new);
    public static final Supplier<BlockEntityType<RunnerBlockEntity>> RUNNER_BLOCK_ENTITY = WenyanBlocks.registerEntity(RunnerBlock.ID, RunnerBlockEntity::new, WenyanBlocks.RUNNER_BLOCK);

    public static final DeferredBlock<CraftingBlock> CRAFTING_BLOCK = WenyanBlocks.DR.registerBlock(CraftingBlock.ID, CraftingBlock::new);
    public static final Supplier<BlockEntityType<CraftingBlockEntity>> CRAFTING_BLOCK_ENTITY = WenyanBlocks.registerEntity(CraftingBlock.ID, CraftingBlockEntity::new, WenyanBlocks.CRAFTING_BLOCK);


    public static final DeferredBlock<PedestalBlock> PEDESTAL_BLOCK = WenyanBlocks.DR.registerBlock(PedestalBlock.ID, PedestalBlock::new);
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_ENTITY = WenyanBlocks.registerEntity(PedestalBlock.ID, PedestalBlockEntity::new, WenyanBlocks.PEDESTAL_BLOCK);

    public static final DeferredBlock<PowerBlock> POWER_BLOCK = WenyanBlocks.DR.registerBlock(PowerBlock.ID, PowerBlock::new);
    public static final Supplier<BlockEntityType<PowerBlockEntity>> POWER_BLOCK_ENTITY = WenyanBlocks.registerEntity(PowerBlock.ID, PowerBlockEntity::new, WenyanBlocks.POWER_BLOCK);

    public static final DeferredBlock<ExplosionModuleBlock> EXPLOSION_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(ExplosionModuleBlock.ID, ExplosionModuleBlock::new);
    public static final Supplier<BlockEntityType<ExplosionModuleEntity>> EXPLOSION_MODULE_ENTITY = WenyanBlocks.registerEntity(ExplosionModuleBlock.ID, ExplosionModuleEntity::new, WenyanBlocks.EXPLOSION_MODULE_BLOCK);

    public static final DeferredBlock<WorldModuleBlock> INFORMATION_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(WorldModuleBlock.ID, WorldModuleBlock::new);
    public static final Supplier<BlockEntityType<WorldModuleEntity>> INFORMATION_MODULE_ENTITY = WenyanBlocks.registerEntity(WorldModuleBlock.ID, WorldModuleEntity::new, WenyanBlocks.INFORMATION_MODULE_BLOCK);

    public static final DeferredBlock<MathModuleBlock> MATH_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(MathModuleBlock.ID, MathModuleBlock::new);
    public static final Supplier<BlockEntityType<MathModuleEntity>> MATH_MODULE_ENTITY = WenyanBlocks.registerEntity(MathModuleBlock.ID, MathModuleEntity::new, WenyanBlocks.MATH_MODULE_BLOCK);

    public static final DeferredBlock<BitModuleBlock> BIT_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(BitModuleBlock.ID, BitModuleBlock::new);
    public static final Supplier<BlockEntityType<BitModuleEntity>> BIT_MODULE_ENTITY = WenyanBlocks.registerEntity(BitModuleBlock.ID, BitModuleEntity::new, WenyanBlocks.BIT_MODULE_BLOCK);

    public static final DeferredBlock<BlockModuleBlock> BLOCK_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(BlockModuleBlock.ID, BlockModuleBlock::new);
    public static final Supplier<BlockEntityType<BlockModuleEntity>> BLOCK_MODULE_ENTITY = WenyanBlocks.registerEntity(BlockModuleBlock.ID, BlockModuleEntity::new, WenyanBlocks.BLOCK_MODULE_BLOCK);

    public static final DeferredBlock<RandomModuleBlock> RANDOM_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(RandomModuleBlock.ID, RandomModuleBlock::new);
    public static final Supplier<BlockEntityType<RandomModuleEntity>> RANDOM_MODULE_ENTITY = WenyanBlocks.registerEntity(RandomModuleBlock.ID, RandomModuleEntity::new, WenyanBlocks.RANDOM_MODULE_BLOCK);

    public static final DeferredBlock<ItemModuleBlock> ITEM_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(ItemModuleBlock.ID, ItemModuleBlock::new);
    public static final Supplier<BlockEntityType<ItemModuleEntity>> ITEM_MODULE_ENTITY = WenyanBlocks.registerEntity(ItemModuleBlock.ID, ItemModuleEntity::new, WenyanBlocks.ITEM_MODULE_BLOCK);

    public static final DeferredBlock<Vec3ModuleBlock> VEC3_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(Vec3ModuleBlock.ID, Vec3ModuleBlock::new);
    public static final Supplier<BlockEntityType<Vec3ModuleEntity>> VEC3_MODULE_ENTITY = WenyanBlocks.registerEntity(Vec3ModuleBlock.ID, Vec3ModuleEntity::new, WenyanBlocks.VEC3_MODULE_BLOCK);

    public static final DeferredBlock<CommunicateModuleBlock> COMMUNICATE_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(CommunicateModuleBlock.ID, CommunicateModuleBlock::new);
    public static final Supplier<BlockEntityType<CommunicateModuleEntity>> COMMUNICATE_MODULE_ENTITY = WenyanBlocks.registerEntity(CommunicateModuleBlock.ID, CommunicateModuleEntity::new, WenyanBlocks.COMMUNICATE_MODULE_BLOCK);

    public static final DeferredBlock<CollectionModuleBlock> COLLECTION_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(CollectionModuleBlock.ID, CollectionModuleBlock::new);
    public static final Supplier<BlockEntityType<CollectionModuleEntity>> COLLECTION_MODULE_ENTITY = WenyanBlocks.registerEntity(CollectionModuleBlock.ID, CollectionModuleEntity::new, WenyanBlocks.COLLECTION_MODULE_BLOCK);

    public static final DeferredBlock<StringModuleBlock> STRING_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(StringModuleBlock.ID, StringModuleBlock::new);
    public static final Supplier<BlockEntityType<StringModuleEntity>> STRING_MODULE_ENTITY = WenyanBlocks.registerEntity(StringModuleBlock.ID, StringModuleEntity::new, WenyanBlocks.STRING_MODULE_BLOCK);

    public static final DeferredBlock<EntityModuleBlock> ENTITY_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(EntityModuleBlock.ID, EntityModuleBlock::new);
    public static final Supplier<BlockEntityType<EntityModuleEntity>> ENTITY_MODULE_ENTITY = WenyanBlocks.registerEntity(EntityModuleBlock.ID, EntityModuleEntity::new, WenyanBlocks.ENTITY_MODULE_BLOCK);

    public static final DeferredBlock<ScreenModuleBlock> SCREEN_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(ScreenModuleBlock.ID, ScreenModuleBlock::new);
    public static final Supplier<BlockEntityType<ScreenModuleBlockEntity>> SCREEN_MODULE_BLOCK_ENTITY = WenyanBlocks.registerEntity(ScreenModuleBlock.ID, ScreenModuleBlockEntity::new, WenyanBlocks.SCREEN_MODULE_BLOCK);

    public static final DeferredBlock<LockModuleBlock> LOCK_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(LockModuleBlock.ID, LockModuleBlock::new);
    public static final Supplier<BlockEntityType<LockModuleEntity>> LOCK_MODULE_ENTITY = WenyanBlocks.registerEntity(LockModuleBlock.ID, LockModuleEntity::new, WenyanBlocks.LOCK_MODULE_BLOCK);

    public static final DeferredBlock<FormationCoreModuleBlock> FORMATION_CORE_MODULE_BLOCK = WenyanBlocks.DR.registerBlock(FormationCoreModuleBlock.ID, FormationCoreModuleBlock::new);
    public static final Supplier<BlockEntityType<FormationCoreModuleEntity>> FORMATION_CORE_MODULE_ENTITY = WenyanBlocks.registerEntity(FormationCoreModuleBlock.ID, FormationCoreModuleEntity::new, WenyanBlocks.FORMATION_CORE_MODULE_BLOCK);

    private static <BE extends BlockEntity> Supplier<BlockEntityType<BE>>
    registerEntity(final String name, final BlockEntityType.BlockEntitySupplier<BE> supplier,
                   final Supplier<? extends Block> block) {
        return WenyanBlocks.DR_ENTITY.register(name, () -> new BlockEntityType<>(supplier, block.get()));
    }
}
