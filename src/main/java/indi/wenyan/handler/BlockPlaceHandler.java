package indi.wenyan.handler;

import indi.wenyan.WenyanNature;
import indi.wenyan.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nullable;

public class BlockPlaceHandler extends JavacallHandler {
    private boolean automatic;// Add this field to the class
    private Level level ;
    private BlockPos pos;
    private ItemStack stack;
    private Player holder;
    private BlockState block;
    private BlockItem item;

    public BlockPlaceHandler(Level world, Player player, BlockState block, BlockPos pos, @Nullable BlockItem item) {
        this.level = world;
        this.holder = player;
        this.block = block;
        this.pos = pos;
        this.item = item;
    }
    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};

    @Override
    public  WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        BlockPos blockPos = pos.offset((int) args[0], (int) args[1], (int) args[2]);
        HandlerEntity.levelRun(holder.level(), (level) -> {
                placeBlock(level, holder, block, blockPos, item);
        });
    return null;
    }

    public static boolean placeBlock(Level world, Player player, BlockState block, BlockPos pos, @Nullable BlockItem item) {
        if(!world.setBlockAndUpdate(pos, block)) {
            WenyanNature.LOGGER.info("Block could not be placed");
            return false;
        }

        // Remove block if placeEvent is canceled
        BlockSnapshot snapshot = BlockSnapshot.create(world.dimension(), world, pos);
        BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
        NeoForge.EVENT_BUS.post(placeEvent);
        if(placeEvent.isCanceled()) {
            world.removeBlock(pos, false);
            return false;
        }

        ItemStack stack;
        if(item == null) stack = new ItemStack(block.getBlock().asItem());
        else {
            stack = new ItemStack(item);
            player.awardStat(Stats.ITEM_USED.get(item));
        }

        // Call OnBlockPlaced method
        block.getBlock().setPlacedBy(world, pos, block, player, stack);

        return true;
    }
}
