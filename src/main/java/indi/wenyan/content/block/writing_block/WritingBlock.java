package indi.wenyan.content.block.writing_block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.client.gui.code_editor.WritingEditorScreen;
import indi.wenyan.client.gui.code_editor.backend.WritingBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.WritingBackendSynchronizer;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.WritingCodePacket;
import indi.wenyan.setup.network.WritingTitlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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
            if (world.isClientSide())
                if (world.getBlockEntity(pos) instanceof WritingBlockEntity tile)
                    openGui(tile.getItemStack(), pos);
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

    // FIXME
    // @OnlyIn(Dist.CLIENT)
    private void openGui(ItemStack runners, BlockPos pos) {
        Minecraft.getInstance().setScreen(new WritingEditorScreen(getCodeEditorBackend(runners, pos)));
    }

    private WritingBlockBackend getCodeEditorBackend(ItemStack runners, BlockPos pos) {
        return new WritingBlockBackend(new WritingBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                runners.set(WyRegistration.PROGRAM_CODE_DATA.get(), content);
                ClientPacketDistributor.sendToServer(new WritingCodePacket(pos, content));
            }

            @Override
            public String getContent() {
                return runners.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            }

            @Override
            public void sendTitle(String title) {
                var warppedTitle = Component.translatable("code.wenyan_programming.bracket", title);
                runners.set(DataComponents.CUSTOM_NAME, warppedTitle);
                ClientPacketDistributor.sendToServer(new WritingTitlePacket(pos, warppedTitle.getString()));

            }

            @Override
            public String getTitle() {
                var title = runners.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString();

                if (title.length() < 2) {
                    return "";
                }
                return title.substring(1, title.length() - 1);
            }
        });
    }
}
