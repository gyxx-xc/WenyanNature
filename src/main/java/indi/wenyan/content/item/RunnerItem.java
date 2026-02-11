package indi.wenyan.content.item;

import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.content.gui.code_editor.backend.CodeEditorBackend;
import indi.wenyan.content.gui.code_editor.backend.CodeEditorBackendSynchronizer;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.RunnerCodePacket;
import indi.wenyan.setup.network.RunnerTitlePacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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

    public final int runningLevel;

    public RunnerItem(Properties properties, int runningLevel) {
        super(Registration.RUNNER_BLOCK.get(), properties);
        this.runningLevel = runningLevel;
    }

    @Override
    public String getDescriptionId() {
        return Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            openGui(itemstack, player, hand);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        if (context.getPlayer().isShiftKeyDown()) {
            context.getItemInHand().set(Registration.RUNNING_TIER_DATA.get(), runningLevel);
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui(ItemStack itemstack, Player player, InteractionHand hand) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
                PacketDistributor.sendToServer(new RunnerCodePacket(slot, content));
            }

            @Override
            public String getContent() {
                return itemstack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), "");
            }

            @Override
            public void sendTitle(String title) {
                int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
                PacketDistributor.sendToServer(new RunnerTitlePacket(slot, title));
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
        var backend = new CodeEditorBackend(List.of(), synchronizer);
        Minecraft.getInstance().setScreen(new CodeEditorScreen(backend));
    }
}
