package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.client.gui.float_note.FloatNoteNamingScreen;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.WYRegistration;
import indi.wenyan.setup.network.DeviceRenamePacket;
import indi.wenyan.setup.network.PlatformRenamePacket;
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

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

    @Override
    public InteractionResult interactLivingEntity (
            ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Player) && target.isAlive()) {
                if (player.isShiftKeyDown()) {
                    if (player.level().isClientSide()) {
                        openGui(target::setCustomName, stack);
                    }
                    return InteractionResult.SUCCESS;
                }
                setName(player.level(), target::setCustomName, stack, player, hand);
                if (!player.level().isClientSide() && target instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }
                return InteractionResult.SUCCESS;
            }

        if (player.isShiftKeyDown()) {
            if (player.level().isClientSide())
                openGui(_ -> {}, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (context.getPlayer() == null)
            return InteractionResult.FAIL;
        switch (blockEntity) {
            case AbstractModuleEntity entity -> {
                if (context.getPlayer().isShiftKeyDown()) {
                    if (level.isClientSide())
                        openGui(component -> setDeviceName(entity, component),
                                context.getItemInHand());
                    return InteractionResult.SUCCESS;
                }
                setName(level, component -> setDeviceName(entity, component), context);
                return InteractionResult.SUCCESS;
            }
            case RunnerBlockEntity entity -> {
                if (level.isClientSide()) {
                    if (context.getPlayer().isShiftKeyDown()) {
                        openGui(component -> setRunnerBlockName(entity, component),
                                context.getItemInHand());
                        return InteractionResult.SUCCESS;
                    }
                    setName(level, component -> setRunnerBlockName(entity, component),
                            context);
                }
                return InteractionResult.SUCCESS;
            }
            case null -> {
                return InteractionResult.FAIL;
            }
            default -> {
                if (context.getPlayer().isShiftKeyDown()) {
                    setName(level, _ -> {}, context);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    private static void setRunnerBlockName(RunnerBlockEntity entity, Component component) {
        entity.setPlatformName(component.getString());
        ClientPacketDistributor.sendToServer(new PlatformRenamePacket(entity.getBlockPos(), component.getString()));
    }

    private static void setDeviceName(AbstractModuleEntity entity, Component component) {
        entity.setPackageName(component.getString());
        ClientPacketDistributor.sendToServer(new DeviceRenamePacket(entity.getBlockPos(), component.getString()));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack item = player.getItemInHand(usedHand);
        if (!super.use(level, player, usedHand).consumesAction() && player.isShiftKeyDown()) {
                if (level.isClientSide())
                    openGui(_ -> {}, item);
                return InteractionResult.SUCCESS;
            }

        return InteractionResult.PASS;
    }

    private void setName(Level level, Consumer<Component> setNameFunc, UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        setName(level, setNameFunc, stack, context.getPlayer(), context.getHand());
    }

    private void setName(Level level, Consumer<Component> setNameFunc, ItemStack stack, @Nullable Player player, InteractionHand hand) {
        if (stack.getOrDefault(WYRegistration.NOTE_LOCK_DATA.get(), false)) {
            setNameFunc.accept(Component.translatable("code.wenyan_programming.bracket", stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty())));
        } else {
            if (level.isClientSide())
                openGui(setNameFunc, stack);
        }
        if (player != null)
            stack.hurtAndBreak(1, player, hand);
    }

//    @Override
//    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
//        return repairCandidate.is(Items.PAPER) || super.isValidRepairItem(stack, repairCandidate);
//    }

    // FIXME
//    @OnlyIn(Dist.CLIENT)
    private void openGui(Consumer<Component> setNameFunc, ItemStack stack) {
        Minecraft.getInstance().setScreen(new FloatNoteNamingScreen(setNameFunc, stack));
    }
}
