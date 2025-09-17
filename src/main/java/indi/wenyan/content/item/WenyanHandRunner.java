package indi.wenyan.content.item;

import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.RunnerCodePacket;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WenyanHandRunner extends BlockItem {
    public final int runningLevel;

    public WenyanHandRunner(Properties properties, int runningLevel) {
        super(Registration.RUNNER_BLOCK.get(), properties);
        this.runningLevel = runningLevel;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull InteractionResultHolder<ItemStack>
    use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new CodeEditorScreen(
                    itemstack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), ""),
                    content -> {
                        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
                        PacketDistributor.sendToServer(new RunnerCodePacket(slot, content));
                    }));
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
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (Objects.requireNonNull(context.getPlayer()).isShiftKeyDown()) {
            context.getItemInHand().set(Registration.RUNNING_TIER_DATA.get(), runningLevel);
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }
}
