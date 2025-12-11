package indi.wenyan.content.item;

import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.content.handler.IImportHandler;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.*;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.RunnerCodePacket;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

// TODO: logic for the item behave like the blocks
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EquipableRunnerItem extends Item implements Equipable, IWenyanPlatform {
    public static final String ID_1 = "equipable_runner";

    private static WenyanProgram program;
    public final int runningLevel;

    @Getter
    private static final ExecQueue execQueue = new ExecQueue();
    private final IImportHandler importFunction = new IImportHandler() {
        @Override
        public WenyanPackage getPackage(String packageName) throws WenyanException.WenyanThrowException {
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
        }

        @Override
        public Optional<IExecReceiver> getExecutor() {
            return Optional.of(EquipableRunnerItem.this);
        }
    };

    public EquipableRunnerItem(Properties properties, int runningLevel) {
        super(properties);
        this.runningLevel = runningLevel;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public InteractionResultHolder<ItemStack>
    use(Level level, Player player, InteractionHand hand) {
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

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            if (slotId == 38) {
                if (program == null || !program.isRunning()) {
                    program = new WenyanProgram(stack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), ""), player, this);
                    program.createMainThread();
                }
                program.step(runningLevel);
                handle(IHandleContext.NONE);
            } else if (program != null && program.isRunning()) program.stop();
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void initEnvironment(WenyanRuntime baseEnvironment) {
        baseEnvironment.setVariable(WenyanPackages.IMPORT_ID, importFunction);
    }

    @Override
    public void notice(JavacallContext context) {
        context.handler().getExecutor().ifPresent(executor -> executor.receive(context));
    }
}
