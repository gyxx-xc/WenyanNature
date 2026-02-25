package indi.wenyan.content.item.additional_module;

import indi.wenyan.content.item.EquipableRunnerItem;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.IWenyanDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class PrintInventoryModule extends Item implements IWenyanDevice {
    public static final String ID = "print_inventory_module";
    // TODO: check to fit itemstack
    @Getter
    public final String packageName = "「a」";

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「a」", (HandlerPackageBuilder.HandlerReturnFunction) (context, request) -> {
                if (!(context instanceof EquipableRunnerItem.ItemContext itemContext)) {
                    throw new WenyanUnreachedException();
                }
                itemContext.player().sendSystemMessage(Component.literal(
                        request.args().getFirst().as(WenyanString.TYPE).value()));
                return WenyanNull.NULL;
            })
            .build();

    public PrintInventoryModule(Properties properties) {
        super(properties);
    }
}
