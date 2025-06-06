package indi.wenyan.content.block;

import indi.wenyan.content.gui.CraftingBlockContainer;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
        assert entity != null;
        if (player.isShiftKeyDown()) {
            entity.setHolder(player);
            entity.isCrafting = true;
        } else {
            if (!level.isClientSide()) {
                MenuProvider provider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("title.wenyan_nature.create_tab");
                    }

                    @Override
                    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new CraftingBlockContainer(i, inventory, entity);
                    }
                };
                player.openMenu(provider, buf -> buf.writeBlockPos(pos));
            }
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
