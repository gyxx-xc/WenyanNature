package indi.wenyan.content.item;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.gui.float_note.FloatNoteNamingScreen;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
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
            @NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!(target instanceof Player)) {
            if (target.isAlive()) {
                if (player.level().isClientSide) {
                    Minecraft.getInstance().setScreen(new FloatNoteNamingScreen(
                            target::setCustomName));
                } else if (target instanceof Mob mob) {
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

        if (blockEntity instanceof AbstractModuleEntity entity) {
            if (level.isClientSide()) {
                Minecraft.getInstance().setScreen(new FloatNoteNamingScreen(
                        component -> entity.setPackageName(component.getString())));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private Component getNamingComponent(ItemStack stack) {
        Component component = stack.get(DataComponents.CUSTOM_NAME);
        if (component != null) {
            return Component.translatable("code.wenyan_programming.bracket", component);
        } else {
            // TODO: open a GUI
            return null;
        }
    }
}
