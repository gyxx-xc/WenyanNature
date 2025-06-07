package indi.wenyan.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends Block implements EntityBlock {

    public static final Properties PROPERTIES = Properties.of();

    public PedestalBlock() {
        super(PROPERTIES);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        PedestalBlockEntity entity = (PedestalBlockEntity) level.getBlockEntity(pos);
        assert entity != null;
        if (entity.canInteract() && !level.isClientSide()) {
            if (stack.isEmpty()) {
                ItemStack extracted = entity.extractItem();
                player.setItemInHand(hand, extracted);
            } else {
                ItemStack remaining = entity.insertItem(stack);
                player.setItemInHand(hand, remaining);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }
}
