package indi.wenyan.handler;

import indi.wenyan.WenyanNature;
import indi.wenyan.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockPlaceHandler extends JavacallHandler {
    private final BlockPos pos;
    private final Player holder;
    private final BlockState block;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};

    public BlockPlaceHandler(Player player, BlockItem block, BlockPos pos) {
        this.holder = player;
        this.block = block.getBlock().defaultBlockState();
        this.pos = pos;
    }

    @Override
    public  WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        BlockPos blockPos = pos.offset((int) args[0], (int) args[1], (int) args[2]);
        HandlerEntity.levelRun(holder.level(), (level) -> placeBlock(level, holder, block, blockPos));
        return null;
    }

    private static void placeBlock(Level world, Player player, BlockState block, BlockPos pos) {
        if(!world.setBlockAndUpdate(pos, block)) {
            WenyanNature.LOGGER.info("Block could not be placed");
            return;
        }

        // Remove block if placeEvent is canceled
        BlockSnapshot snapshot = BlockSnapshot.create(world.dimension(), world, pos);
        BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
        NeoForge.EVENT_BUS.post(placeEvent);
        if(placeEvent.isCanceled()) {
            world.removeBlock(pos, false);
            return;
        }

        // Call OnBlockPlaced method
        ItemStack stack = new ItemStack(block.getBlock().asItem());
        block.getBlock().setPlacedBy(world, pos, block, player, stack);
    }
}