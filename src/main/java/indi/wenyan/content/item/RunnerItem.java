package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.client.gui.code_editor.RunnerBlockScreen;
import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditorBackendSynchronizer;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.server.RunnerCodePacket;
import indi.wenyan.setup.network.server.RunnerTitlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerItem extends BlockItem {
    // String constants for registry names and entity IDs
    public static final String ID_0 = "hand_runner_0";
    public static final String ID_1 = "hand_runner_1";
    public static final String ID_2 = "hand_runner_2";
    public static final String ID_3 = "hand_runner_3";
    public static final String ID_4 = "hand_runner_4";
    public static final String ID_5 = "hand_runner_5";
    public static final String ID_6 = "hand_runner_6";

    public final int runningLevel;

    public RunnerItem(Properties properties, int runningLevel) {
        super(switch (runningLevel) {
            case 0 -> WenyanBlocks.RUNNER_BLOCK_0.get();
            case 1 -> WenyanBlocks.RUNNER_BLOCK_1.get();
            case 2 -> WenyanBlocks.RUNNER_BLOCK_2.get();
            case 3 -> WenyanBlocks.RUNNER_BLOCK_3.get();
            case 4 -> WenyanBlocks.RUNNER_BLOCK_4.get();
            case 5 -> WenyanBlocks.RUNNER_BLOCK_5.get();
            case 6 -> WenyanBlocks.RUNNER_BLOCK_6.get();
            default -> throw new IllegalArgumentException("Invalid running level: " + runningLevel);
        }, properties);
        this.runningLevel = runningLevel;
    }

    // FIXME: @Rokidna V
//    @Override
//    public String getDescriptionId() {
//        return Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
//    }

//    @Override
//    public InteractionResult use(Level level, Player player, InteractionHand hand) {
//        ItemStack itemstack = player.getItemInHand(hand);
//        if (level.isClientSide()) {
//            openGui(itemstack, player, hand);
//        }
//        return InteractionResult.SUCCESS;
//    }

    // FIXME
    // @Override
    // public boolean onDroppedByPlayer(@NotNull ItemStack item, Player player) {
    // if (!player.isShiftKeyDown()) {
    // var codeData = item.get(Registration.PROGRAM_CODE_DATA.get());
    // if (codeData != null) {
    // HandRunnerEntity handRunnerEntity = new HandRunnerEntity(player,
    // codeData, runningLevel);
    // player.level().addFreshEntity(handRunnerEntity);
    //
    // item.shrink(1);
    // return false; // not gen an item entity
    // } // else : go outside
    // }
    // return super.onDroppedByPlayer(item, player);
    // }

//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        if (context.getPlayer() == null) return InteractionResult.FAIL;
//        if (context.getPlayer().isShiftKeyDown()) {
//            return super.useOn(context);
//        }
//        return InteractionResult.PASS;
//    }

//    @OnlyIn(Dist.CLIENT)
    private void openGui(ItemStack itemstack, Player player, InteractionHand hand) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : 40;
                ClientPacketDistributor.sendToServer(new RunnerCodePacket(slot, content));
            }

            @Override
            public String getContent() {
                return itemstack.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            }

            @Override
            public void sendTitle(String title) {
                int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : 40;
                ClientPacketDistributor.sendToServer(new RunnerTitlePacket(slot, title));
            }

            @Override
            public String getTitle() {
                return itemstack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString();
            }

            @Override
            public Deque<Component> getOutput() {
                return new ArrayDeque<>();
            }

            @Override
            public boolean isOutputChanged() {
                return false;
            }
        };
        var backend = new RunnerBlockBackend(List.of(), synchronizer);
        Minecraft.getInstance().setScreen(new RunnerBlockScreen(backend));
    }
}
