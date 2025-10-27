package indi.wenyan.content.item;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.gui.float_note.FloatNoteNamingScreen;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FloatNoteItem extends Item {
    public static final String ID = "float_note";

    public FloatNoteItem(Properties properties) {
        super(properties.durability(10));
    }

    public InteractionResult interactLivingEntity(
            ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Player)) {
            if (target.isAlive()) {
                if (player.isShiftKeyDown()) {
                    openGui(player.level(), target::setCustomName, stack);
                    return InteractionResult.sidedSuccess(player.level().isClientSide());
                }
                setName(player.level(), target::setCustomName, stack, player, hand);
                if (!player.level().isClientSide() && target instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }
                return InteractionResult.sidedSuccess(player.level().isClientSide());
            }
        }
        if (player.isShiftKeyDown()) {
            openGui(player.level(), component -> {

            }, stack);
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        assert context.getPlayer() != null;
        if (blockEntity instanceof AbstractModuleEntity entity) {
            if (context.getPlayer().isShiftKeyDown()) {
                openGui(level, component -> entity.setPackageName(component.getString()),
                        context.getItemInHand());
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            setName(level, component -> entity.setPackageName(component.getString()), context);
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            if (context.getPlayer().isShiftKeyDown()) {
                setName(level, component -> {}, context);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack item = player.getItemInHand(usedHand);
        if (!super.use(level, player, usedHand).getResult().consumesAction()) {
            if (player.isShiftKeyDown()) {
                openGui(level, component -> {

                }, item);
                return InteractionResultHolder.sidedSuccess(item,
                        level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(item);
    }

    private void setName(Level level, Consumer<Component> setNameFunc, UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        setName(level, setNameFunc, stack, context.getPlayer(), context.getHand());
    }

    private void setName(Level level, Consumer<Component> setNameFunc, ItemStack stack, @Nullable Player player, InteractionHand hand) {
        if (stack.getOrDefault(Registration.NOTE_LOCK_DATA.get(), false)) {
            setNameFunc.accept(stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()));
        } else {
            openGui(level, setNameFunc, stack);
        }
        if (player != null)
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(Items.PAPER) || super.isValidRepairItem(stack, repairCandidate);
    }

    private void openGui(Level level, Consumer<Component> setNameFunc, ItemStack stack) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new FloatNoteNamingScreen(setNameFunc, stack));
        }
    }
}
