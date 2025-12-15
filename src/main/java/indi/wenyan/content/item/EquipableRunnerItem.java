package indi.wenyan.content.item;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.handler.IImportHandler;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.ItemContext;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.RunnerCodePacket;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EquipableRunnerItem extends Item implements Equipable, IWenyanPlatform {
    public static final String ID_1 = "equipable_runner";

    private static WenyanProgram program;
    public final int runningLevel;

    @Getter
    private final ExecQueue execQueue = new ExecQueue();

    private final IImportHandler importFunction = (context, packageName) -> {
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
                rawPackage.functions().forEach((name, function) -> {
                    // TODO
//                    map.put(name, (IHandlerWarper) function::handle);
                });
                return new WenyanPackage(map);
            }
        }

        throw new WenyanException("No runner device found");
    };

//    public static final BlockCapability<ResourceHandler<ItemResource>, @Nullable WenyanProgram> ITEM_HANDLER_BLOCK =
//            BlockCapability.create(
//                    // Provide a name to uniquely identify the capability.
//                    ResourceLocation.fromNamespaceAndPath("mymod", "item_handler"),
//                    // Provide the queried type. Here, we want to look up `ResourceHandler<ItemResource>` instances.
//                    ResourceHandler.asClass(),
//                    // Provide the context type. We will allow the query to receive an extra `WenyanProgram side` parameter.
//                    WenyanProgram.class);

    public static final String EQUIPABLE_PROGRAM_ID = "equipable_program";
    public static final ItemCapability<WenyanProgram, Void> ITEM_HANDLER_ITEM =
            ItemCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, EQUIPABLE_PROGRAM_ID),
                    WenyanProgram.class);

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
        // FIXME
//        if (!level.isClientSide() && entity instanceof Player player) {
//            var program = stack.getCapability(ITEM_HANDLER_ITEM);
//            if (slotId == 38) {
//                if (program == null || !program.isRunning()) {
//                    program = new WenyanProgram(stack.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), ""),
//                            player, this);
//                    program.createMainThread();
//                }
//                program.step(runningLevel);
//                handle(new ItemContext(stack, level, player, slotId, isSelected));
//                // FIXME: isRunning might be laggy (lock required in atomic int)
//            } else if (program != null && program.isRunning()) {
//                program.stop();
//            }
//        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void initEnvironment(WenyanRuntime baseEnvironment) {
        baseEnvironment.setVariable(WenyanPackages.IMPORT_ID, importFunction);
    }
}
