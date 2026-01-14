package indi.wenyan.content.item;

import indi.wenyan.content.gui.code_editor.CodeEditorBackend;
import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.RunnerCodePacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerItem extends BlockItem {
    public static final String ID_1 = "hand_runner";
    // String constants for registry names and entity IDs
    public static final String ID_0 = "hand_runner_0";
    public static final String ID_2 = "hand_runner_2";
    public static final String ID_3 = "hand_runner_3";

    public final int runningLevel;

    public RunnerItem(Properties properties, int runningLevel) {
        super(Registration.RUNNER_BLOCK.get(), properties);
        this.runningLevel = runningLevel;
    }

    @Override
    public InteractionResultHolder<ItemStack>
    use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            opengui(itemstack, player, hand);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    // FIXME
//    @Override
//    public boolean onDroppedByPlayer(@NotNull ItemStack item, Player player) {
//        if (!player.isShiftKeyDown()) {
//            var codeData = item.get(Registration.PROGRAM_CODE_DATA.get());
//            if (codeData != null) {
//                HandRunnerEntity handRunnerEntity = new HandRunnerEntity(player,
//                        codeData, runningLevel);
//                player.level().addFreshEntity(handRunnerEntity);
//
//                item.shrink(1);
//                return false; // not gen an item entity
//            } // else : go outside
//        }
//        return super.onDroppedByPlayer(item, player);
//    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (Objects.requireNonNull(context.getPlayer()).isShiftKeyDown()) {
            context.getItemInHand().set(Registration.RUNNING_TIER_DATA.get(), runningLevel);
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @OnlyIn(Dist.CLIENT)
    private void opengui(ItemStack itemstack, Player player, InteractionHand hand) {
        var persistentData = new CodeEditorBackend.PersistentData(itemstack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), ""), "", List.of());
        Minecraft.getInstance().setScreen(new CodeEditorScreen(persistentData,
                content -> {
                    int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
                    PacketDistributor.sendToServer(new RunnerCodePacket(slot, content));
                }));
    }
}
