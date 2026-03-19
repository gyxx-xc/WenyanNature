package indi.wenyan.content.block.writing_block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.network.client.BlockSetScreenPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WritingBlock extends Block implements EntityBlock {
    public static final String ID = "writing_block";

    public WritingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WritingBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide() && player instanceof ServerPlayer sp)
                PacketDistributor.sendToPlayer(sp, new BlockSetScreenPacket(pos, "writing_block_set_screen"));
            return InteractionResult.SUCCESS;
        }
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof WritingBlockEntity tile) {
            var itemResource = ResourceHandlerUtil.extractFirst(tile.getItemHandler(), _ -> true, 64, null);
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(),
                    itemResource == null ? ItemStack.EMPTY : itemResource.resource().toStack(itemResource.amount()));
            world.addFreshEntity(item);
            if (!player.getItemInHand(handIn).isEmpty()) {
                if (ResourceHandlerUtil.isValid(tile.getItemHandler(), ItemResource.of(player.getInventory().getSelectedItem()))) {
                    ItemUtil.insertItemReturnRemaining(tile.getItemHandler(),
                            player.getItemInHand(handIn), false, null);
                    player.setItemInHand(handIn, ItemStack.EMPTY);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
