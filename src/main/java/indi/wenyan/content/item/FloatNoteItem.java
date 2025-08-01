package indi.wenyan.content.item;

import indi.wenyan.content.block.additional_module.AbstractAdditionalModuleEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class FloatNoteItem extends Item {
    public static final String ID = "float_note";

    public FloatNoteItem(Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResult interactLivingEntity(
            ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        Component component = stack.get(DataComponents.CUSTOM_NAME);
        if (component != null && !(target instanceof Player)) {
            if (!player.level().isClientSide && target.isAlive()) {
                target.setCustomName(component);
                if (target instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }
            }

            return InteractionResult.sidedSuccess(player.level().isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());

        if (blockEntity instanceof AbstractAdditionalModuleEntity entity) {
            Component component = context.getItemInHand().get(DataComponents.CUSTOM_NAME);
            if (component != null) {
                entity.setPackageName(component.toString());
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }
}
