package indi.wenyan.content.block.furnace;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.GuiText;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LogicFurnaceBlock extends Block implements EntityBlock {
    public static final String ID = "logic_furnace";

    public LogicFurnaceBlock(Properties properties) {
        super(properties.destroyTime(0.3F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LogicFurnaceBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof LogicFurnaceBlockEntity entity))
            return InteractionResult.FAIL;
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, _) ->
                            new LogicFurnaceMenu(containerId, inventory,
                                    ContainerLevelAccess.create(level, pos),
                                    entity.getData(),
                                    entity.getInput(),
                                    entity.getOutput()),
                    GuiText.FurnaceTitle.text()
            ));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return (level1, blockPos, blockState1, t) -> {
            if (type == WenyanBlocks.LOGIC_FURNACE_ENTITY.get() && t instanceof LogicFurnaceBlockEntity entity && level1 instanceof ServerLevel sl) {
                entity.tick(sl, blockPos, blockState1, level.getRandom());
            }
        };
    }
}
