package indi.wenyan.content.handler;

import indi.wenyan.content.block.RunnerBlock;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockPlaceHandler implements IJavacallHandler {
    private final BlockPos pos;
    private final BlockPos attach;
    private final Player holder;
    private final BlockState block;

    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanInteger.TYPE, WenyanInteger.TYPE, WenyanInteger.TYPE};

    public BlockPlaceHandler(Player player, BlockItem block, BlockPos pos, BlockState self) {
        this.holder = player;
        this.block = block.getBlock().defaultBlockState();
        this.pos = pos;
        this.attach = pos.relative(RunnerBlock.getConnectedDirection(self).getOpposite());
    }

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        var args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        BlockPos blockPos = pos.offset(
                Math.max(-10, Math.min(10, (int) args.get(0))),
                Math.max(-10, Math.min(10, (int) args.get(1))),
                Math.max(-10, Math.min(10, (int) args.get(2))));
        placeBlock(holder.level(), holder, blockPos, attach);
        return WenyanNull.NULL;
    }

    private static void placeBlock(Level world, Player player, BlockPos pos, BlockPos attach) {

        // TODO: change this to a real block
        BlockState block = world.getBlockState(attach);

        if(!world.setBlockAndUpdate(pos, block)) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_data_type").getString());
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
