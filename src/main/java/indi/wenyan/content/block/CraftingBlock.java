package indi.wenyan.content.block;

import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CraftingBlock extends Block implements EntityBlock {
    public static final Properties PROPERTIES = Properties.of();
    public static final String ID = "crafting_block";

    public CraftingBlock() {
        super(PROPERTIES);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new CraftingBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        CraftingBlockEntity entity = (CraftingBlockEntity) level.getBlockEntity(pos);
        if (!level.isClientSide()) {
            assert entity != null;
            player.openMenu(new SimpleMenuProvider(entity, Component.empty()), pos);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (type, pos, state1, entity) -> {
            if (blockEntityType == Registration.CRAFTING_ENTITY.get())
                CraftingBlockEntity.tick(level, pos, state1, (CraftingBlockEntity) entity);
        };
    }
}
