package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.IRenamable;
import indi.wenyan.judou.utils.function.ChineseUtils;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.client.FloatNoteSetScreenPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FloatNoteItem extends Item {
    public static final String ID = "float_note";

    public FloatNoteItem(Properties properties) {
        super(properties.durability(10));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (context.getPlayer() == null)
            return InteractionResult.PASS;
        if (!(blockEntity instanceof IRenamable entity))
            return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        if (stack.getOrDefault(WyRegistration.NOTE_LOCK_DATA.get(), false)) {
            String newName = ChineseUtils.bracketOf(stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString());
            entity.setName(newName);
        } else {
            if (context.getPlayer() instanceof ServerPlayer sp)
                PacketDistributor.sendToPlayer(sp, new FloatNoteSetScreenPacket(context.getClickedPos(), context.getHand()));
        }

        Player player = context.getPlayer();
        if (player != null)
            stack.hurtAndBreak(1, player, context.getHand());
        return InteractionResult.SUCCESS;
    }
}
