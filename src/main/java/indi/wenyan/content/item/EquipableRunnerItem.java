package indi.wenyan.content.item;

import com.mojang.datafixers.util.Either;
import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.handler.RequestCallHandler;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.IHandleableRequest;
import indi.wenyan.interpreter.exec_interface.structure.ImportRequest;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EquipableRunnerItem extends Item implements Equipable, IWenyanPlatform {
    public static final String ID_1 = "equipable_runner";

    // STUB: may need to find a better way to do it;
    //   maybe after the program storage is done?
    private static final Map<Integer, WenyanProgram> PROGRAMS = new HashMap<>();
    public final int runningLevel;

    @Getter
    private final ExecQueue execQueue = new ExecQueue();
    private final RequestCallHandler importFunction = (t, s, a) ->
            new ImportRequest(t, this, this::getPackage, a);

    @Getter @Setter
    private String platformName = Component.translatable("code.wenyan_programming.bracket", Component.translatable("item.wenyan_programming.equipable_runner")).getString();

    public EquipableRunnerItem(Properties properties, int runningLevel) {
        super(properties);
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

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            var program = PROGRAMS.get(stack.hashCode());
            if (slotId == 38) {
                if (program == null) {
                    // run program
                    var newProgram = new WenyanProgram(this);
                    PROGRAMS.put(stack.hashCode(), newProgram);
                } else {
                    if (!program.isRunning()) {
                        try {
                            program.createThread(stack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), ""));
                        } catch (WenyanThrowException e) {
                            handleError(e.getMessage());
                        }
                    }
                    program.step(runningLevel);
                    handle(new ItemContext(stack, level, player, slotId, isSelected));
                }
            } else if (program != null && program.isRunning()) {
                // FIXME: isRunning might be laggy (lock required in atomic int)
                program.stop();
                PROGRAMS.remove(stack.hashCode());
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public WenyanRuntime initEnvironment() {
        var baseEnvironment = IWenyanPlatform.super.initEnvironment();
        baseEnvironment.setVariable(WenyanPackages.IMPORT_ID, importFunction);
        return baseEnvironment;
    }

    @Override
    public void handleError(String error) {
//        WenyanException.handleException(player, error);
    }

    @Override
    public void notice(IHandleableRequest request, IHandleContext context) throws WenyanThrowException {
        if (!(context instanceof ItemContext itemContext)) {
            throw new WenyanException.WenyanUnreachedException();
        }
        if (!(request instanceof ItemRequest itemRequest))
            throw new WenyanException.WenyanUnreachedException();

        ItemStack current = itemContext.player().getInventory().getItem(itemRequest.slotId());
        // STUB: better equal
        if (!current.equals(itemRequest.itemStack()))
            throw new WenyanException("item changed");
    }

    private Either<WenyanPackage, WenyanThread> getPackage(IHandleContext context, String packageName) throws WenyanThrowException {
        if (!(context instanceof ItemContext itemContext)) {
            throw new WenyanException("Context is not an instance of ItemContext");
        }

        if (!(itemContext.player() instanceof ServerPlayer player)) {
            throw new WenyanException("Entity is not a ServerPlayer");
        }

        int inventorySize = player.getInventory().getContainerSize();
        for (int i = 0; i < inventorySize; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IWenyanDevice device) {
                var rawPackage = device.getExecPackage();
                var map = new HashMap<>(rawPackage.variables());
                int finalSlot = i;
                rawPackage.functions().forEach(
                        (name, function) ->
                                map.put(name, (RequestCallHandler) (thread, self, args) ->
                                        new ItemRequest(this, thread, function.get(),
                                                self, args, stack, finalSlot))); // am I writing lisp?
                return Either.left(new WenyanPackage(map));
            }
        }

        throw new WenyanException("No runner device found");
    }

    private record ItemRequest(
            IWenyanPlatform platform,
            WenyanThread thread,
            IRawRequest request,
            IWenyanValue self,
            List<IWenyanValue> args,
            ItemStack itemStack,
            int slotId
    ) implements IHandleableRequest {
        @Override
        public boolean handle(IHandleContext context) throws WenyanThrowException {
            return request.handle(context, this);
        }
    }

    public record ItemContext(
            ItemStack itemStack,
            Level level,
            Player player,
            int slotId,
            boolean isSelected
    ) implements IHandleContext {
    }

    @OnlyIn(Dist.CLIENT)
    private void opengui(ItemStack itemstack, Player player, InteractionHand hand) {
//        var presis
//        Minecraft.getInstance().setScreen(new CodeEditorScreen(
//                ,
//                content -> {
//                    int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
//                    PacketDistributor.sendToServer(new RunnerCodePacket(slot, content));
//                }));
    }
}
