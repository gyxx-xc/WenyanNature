package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.CraftingBlockEntity;
import indi.wenyan.content.gui.TextFieldScreen;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockRunnerCodePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class
RunnerBlock extends AbstractFuluBlock implements EntityBlock {
    public static final String ID = "runner_block";

    @OnlyIn(Dist.CLIENT)
    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var runner = (RunnerBlockEntity) level.getBlockEntity(pos);
        assert runner != null;
        if (player.isShiftKeyDown()) {
            if (level.isClientSide())
                Minecraft.getInstance().setScreen(new TextFieldScreen(runner.pages, content -> {
                    runner.pages = content;
                    runner.setChanged();
                    PacketDistributor.sendToServer(new BlockRunnerCodePacket(pos, content));
                }));
        } else {
            if (!level.isClientSide()) {
                var maybeCrafting = level.getBlockEntity(
                        pos.relative(getConnectedDirection(state).getOpposite()));

                if (maybeCrafting instanceof CraftingBlockEntity cb) {
                    cb.run(runner, player);
                } else {
                    runner.run(player);
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RunnerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == Registration.RUNNER_BLOCK_ENTITY.get())
                ((RunnerBlockEntity) entity).tick(level1, pos, state1);
        };
    }
}
